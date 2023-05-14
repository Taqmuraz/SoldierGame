
public class LinkedList<T> implements Collection<T>
{
    interface Node<T>
    {
        void iterate(Consumer<T> consumer);
    }
    static class EmptyNode<T> implements Node<T>
    {
        @Override
        public void iterate(Consumer<T> consumer) { }
    }
    interface NodeAction<T>
    {
        void call(Node<T> node);
    }
    interface NodeProvider<T>
    {
        void provide(NodeAction<T> action);
    }
    static class ChainNode<T> implements Node<T>
    {
        NodeProvider<T> provider;
        T item;

        public ChainNode(T item, NodeProvider<T> provider)
        {
            this.provider = provider;
            this.item = item;
        }

        @Override
        public void iterate(Consumer<T> consumer)
        {
            consumer.item(item);
            provider.provide(node -> node.iterate(consumer));
        }
    }

    interface RemoveHandler
    {
        void remove();
    }

    interface ItemFactory<T>
    {
        T create(RemoveHandler remove);
    }

    static class RemoveConditionNodeProvider<T> implements NodeProvider<T>
    {
        boolean condition;
        NodeProvider<T> last;
        Node<T> current;

        public RemoveConditionNodeProvider(NodeProvider<T> last, ItemFactory<T> factory) {
            this.last = last;
            this.current = new ChainNode<T>(factory.create(() -> this.condition = true), last);
        }

        @Override
        public void provide(NodeAction<T> action)
        {
            if(condition) last.provide(action);
            else action.call(current);
        }
    }
    static class EmptyNodeProvider<T> implements NodeProvider<T>
    {
        @Override
        public void provide(NodeAction<T> action) { action.call(new EmptyNode<T>()); }
    }

    NodeProvider<T> last = new EmptyNodeProvider<T>();

    void addItem(ItemFactory<T> factory)
    {
        last = new RemoveConditionNodeProvider<T>(last, factory);
    }

    @Override
    public void iterate(Consumer<T> consumer)
    {
        last.provide(node -> node.iterate(consumer));
    }
}