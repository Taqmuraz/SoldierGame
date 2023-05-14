import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Example
{
    public static void main(String[] args)
    {
        SoldierGame game = new SoldierGame();
        try(Scanner scanner = new Scanner(new FileInputStream(new File("Input.txt"))))
        {
            System.out.println("Input the soldiers number");
            int count = scanner.nextInt();
            for(int i = 0; i < count; i++)
            {
                System.out.println("Input a name");
                String name = scanner.next();
                game.createSoldier(death -> new MortalSoldier(name, () ->
                {
                    death.death();
                    System.out.print("%s is dead. Soldiers alive : ".formatted(name));
                    game.soldiers().iterate(soldier ->
                    {
                        System.out.print("[");
                        soldier.printStatus(System.out::print);
                        System.out.print("]");
                    });
                    System.out.println();
                }));
            }
            System.out.print("Game started. Soldiers alive : ");
            game.soldiers().iterate(soldier ->
            {
                System.out.print("[");
                soldier.printStatus(System.out::print);
                System.out.print("]");
            });
            System.out.println();
            game.soldiers().iterate(soldier ->
            {
                soldier.takeDamage(100f);
            });
        }
        catch(Throwable th)
        {
            System.out.println(th);
        }
    }
}