
public class LinkedList<T> implements Collection<T>
{
    interface RemoveAction
    {
        void remove();
    }
    interface ItemFactory<T>
    {
        T create(RemoveAction remove);
    }
    class Node
    {
        private T item;
        private boolean hasItem;
        Node next;

        void put(T item)
        {
            hasItem = true;
            this.item = item;
        }

        void iterate(Consumer<T> consumer)
        {
            if(hasItem) consumer.item(item);
            if(next != null) next.iterate(consumer);
        }
    }

    public LinkedList()
    {
        first = last = new Node();
    }

    Node first;
    Node last;

    void addItem(ItemFactory<T> factory)
    {
        Node next = new Node();
        Node prev = last;
        T item = factory.create(() ->
        {
            prev.next = next.next;
        });
        next.put(item);
        last.next = next;
        last = next;
    }

    @Override
    public void iterate(Consumer<T> consumer)
    {
        first.iterate(consumer);
    }
}