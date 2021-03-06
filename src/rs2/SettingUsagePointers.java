package rs2;


public class SettingUsagePointers {

    public static void unpackConfig(JagexArchive jagexArchive)
    {
        Packet stream = new Packet(jagexArchive.getDataForName("varp.dat"));
        int cacheSize = stream.g2();
        if(cache == null)
            cache = new SettingUsagePointers[cacheSize];
        for(int j = 0; j < cacheSize; j++)
        {
            if(cache[j] == null)
                cache[j] = new SettingUsagePointers();
            cache[j].readValues(stream);
        }
        if(stream.pos != stream.data.length)
            System.out.println("varptype load mismatch");
    }

    private void readValues(Packet stream)
    {
        do
        {
            int j = stream.g1();
            if(j == 0)
                return;
            //int dummy;
            if(j == 1)
                 stream.g1();
            else
            if(j == 2)
                stream.g1();
            else
            if(j == 3)
            	{}
            else
            if(j == 4)
                {}//dummy = 2;
            else
            if(j == 5)
                usage = stream.g2();
            else
            if(j == 6)
                {}//dummy = 2;
            else
            if(j == 7)
                stream.g4();
            else
            if(j == 8)
                {}//aBoolean713 = true;
             else
            if(j == 10)
                 stream.gstr();
            else
            if(j == 11)
                {}//aBoolean713 = true;
            else
            if(j == 12)
                stream.g4();
            else
            if(j == 13)
                {}//dummy = 2;
            else
                System.out.println("Error unrecognised config code: " + j);
        } while(true);
    }

    private SettingUsagePointers()
    {
    }

    public static SettingUsagePointers cache[];
    public int usage;

}
