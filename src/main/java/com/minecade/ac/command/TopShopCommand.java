@MinecadeCommand(commandName="topshop")
public class TopShopCommand implements CommandExecutor {
    
    private final ACGame game;

    /**
     * Top Shop command constructor
     * @param game
     * @author Kvnamo
     */
    public TopShopCommand(final ACGame game) {
        this.game = game;
    }

    /**
     * On topshop command
     * @author Kvnamo
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) game.goToTopShop(sender); 
        else sender.sendMessage(String.format("%sYou need to be in the game to do this!", ChatColor.RED));

        return true;
}