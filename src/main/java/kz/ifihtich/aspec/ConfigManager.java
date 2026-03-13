package kz.ifihtich.aspec;

public class ConfigManager {

    public String getString(String path){
        return Utils.color(ASpec.getInstance().getConfig().getString(path));
    }

    public int getInt(String path){
        return ASpec.getInstance().getConfig().getInt(path);
    }


}
