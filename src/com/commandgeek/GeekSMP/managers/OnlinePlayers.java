public class OnlinePlayers {
    /**
     * Method return type-safe version of Bukkit::getOnlinePlayers
     * @return {@code ArrayList} containing online players
     */
    public static List<Player> getOnlinePlayers {
        List<Player> onlinePlayers = new ArrayList<>();

        try {
            Method onlinePlayerMethod = Server.class.getMethod("getOnlinePlayers");
            if (onlinePlayerMethod.getReturnType().equals(Collection.class)) {
                for (Object o : ((Collection<?>) onlinePlayerMethod.invoke(Bukkit.getServer()))) {
                    onlinePlayers.add((Player) o);
                }
            } else {
                Collections.addAll(onlinePlayers, ((Player[]) onlinePlayerMethod.invoke(Bukkit.getServer())));
            }
			return onlinePlayers;
        }
    }
}