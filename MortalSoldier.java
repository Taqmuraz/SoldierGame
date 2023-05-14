
public class MortalSoldier implements Soldier
{
    float health = 100;
    String name;
    DeathHandler death;

    public MortalSoldier(String name, DeathHandler death)
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