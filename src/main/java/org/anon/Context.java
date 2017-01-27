package org.anon;

public class Context {

    private String nameAndVersion;
    private static Context INSTANCE;

    private Context() {
        //get program name and version from manifest parameters
        String name = null;
        String version = null;
        String result = null;
        Package pkg = getClass().getPackage();
        if (pkg != null) {
            name = pkg.getImplementationTitle();
            if (name != null) {
                version = pkg.getImplementationVersion();
                nameAndVersion = name + " " + version;
            } else {
                name = pkg.getSpecificationTitle();
                if (name != null) {
                    version = pkg.getSpecificationVersion();
                    nameAndVersion = name + " " + version;
                } else {
                    nameAndVersion = null;
                }
            }
        } else {
            nameAndVersion = null;
        }
    }

    public static Context getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Context();
        }
        return INSTANCE;
    }

    public String getNameAndVersion() {
        return nameAndVersion;
    }

}
