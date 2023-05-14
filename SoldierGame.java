
public class SoldierGame
{
    LinkedList<Soldier> soldiers = new LinkedList<Soldier>();

    public void createSoldier(SoldierFactory factory)
    {
        soldiers.addItem(remove -> factory.create(remove::remove));
    }

    public Collection<Soldier> soldiers()
    {
        return soldiers;
    }
}