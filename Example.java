import java.util.Scanner;

interface Collection<T>
{
    void iterate(Consumer<T> consumer);
}
interface Consumer<T>
{
    void item(T item);
}

class SoldierGame
{
    interface Node
    {
        void iterate(Consumer<Soldier> consumer);
    }
    static class EmptyNode implements Node
    {
        static final Node instance = new EmptyNode();
        @Override
        public void iterate(Consumer<Soldier> consumer) { }
    }
    interface NodeAction
    {
        void call(Node node);
    }
    interface NodeProvider
    {
        void provide(NodeAction action);
    }
    static class ChainNode implements Node
    {
        NodeProvider provider;
        Soldier soldier;

        public ChainNode(Soldier soldier, NodeProvider provider)
        {
            this.provider = provider;
            this.soldier = soldier;
        }

        @Override
        public void iterate(Consumer<Soldier> consumer)
        {
            consumer.item(soldier);
            provider.provide(node -> node.iterate(consumer));
        }
    }

    public interface SoldierFactory
    {
        Soldier create(DeathHandler death);
    }

    static class DeathConditionNodeProvider implements NodeProvider
    {
        boolean condition;
        NodeProvider last;
        Node current;

        public DeathConditionNodeProvider(NodeProvider last, SoldierFactory factory) {
            this.last = last;
            this.current = new ChainNode(factory.create(() -> this.condition = true), last);
        }

        @Override
        public void provide(NodeAction action)
        {
            if(condition) last.provide(action);
            else action.call(current);
        }
    }
    static class EmptyNodeProvider implements NodeProvider
    {
        @Override
        public void provide(SoldierGame.NodeAction action) { action.call(EmptyNode.instance); }
    }

    NodeProvider last = new EmptyNodeProvider();

    public void createSoldier(SoldierFactory factory)
    {
        last = new DeathConditionNodeProvider(last, factory);
    }

    public void iterate(Consumer<Soldier> consumer)
    {
        last.provide(node -> node.iterate(consumer));
    }
}

interface DeathHandler
{
    void death();    
}

interface Printer
{
    void print(String text);
}

interface Soldier
{
    void printStatus(Printer printer);
    void takeDamage(float damage);
}

class EnemySoldier implements Soldier
{
    float health = 100;
    String name;
    DeathHandler death;

    public EnemySoldier(String name, DeathHandler death)
    {
        this.name = name;
        this.death = death;
    }

    public void takeDamage(float damage)
    {
        if(health >= 0)
        {
            health -= damage;
            if(health <= 0) death.death();
        }
    }

    @Override
    public void printStatus(Printer printer)
    {
        printer.print(name);
        printer.print(", health = ");
        printer.print(Float.toString(health));
    }
}

public class Example
{
    public static void main(String[] args)
    {
        SoldierGame game = new SoldierGame();
        try(Scanner scanner = new Scanner(System.in))
        {
            System.out.println("Input the soldiers number");
            int count = scanner.nextInt();
            for(int i = 0; i < count; i++)
            {
                System.out.println("Input a name");
                String name = scanner.next();
                game.createSoldier(death -> new EnemySoldier(name, () ->
                {
                    death.death();
                    System.out.print("%s is dead. Soldiers alive : ".formatted(name));
                    game.iterate(soldier ->
                    {
                        System.out.print("[");
                        soldier.printStatus(System.out::print);
                        System.out.print("]");
                    });
                    System.out.println();
                }));
            }
            System.out.print("Game started. Soldiers alive : ");
            game.iterate(soldier ->
            {
                System.out.print("[");
                soldier.printStatus(System.out::print);
                System.out.print("]");
            });
            System.out.println();
            game.iterate(soldier ->
            {
                soldier.takeDamage(100f);
            });
        }
    }
}