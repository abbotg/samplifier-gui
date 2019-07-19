//package nzero.samplifier.profile;
//
//import nzero.samplifier.model.Register;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//
///**
// * Stores data for the entire set of registers
// */
//class Profile {
//    private String name;
//    private List<Register> registers;
//
//    Profile(String name, List<Register> registers) {
//        this.name = name;
//        this.registers = registers;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public List<Register> getRegisters() {
//        return registers;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Profile profile = (Profile) o;
//
//        return name.equals(profile.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return name.hashCode();
//    }
//}

package nzero.samplifier.profile;

import nzero.samplifier.model.Register;

import java.util.*;

public class Profile {
    private String name;
    private Map<String, String> registerData;

    /**
     * For building profiles live
     *
     * @param name
     * @param registerList
     */
    public Profile(String name, List<Register> registerList) {
        this.name = name;
        this.registerData = new LinkedHashMap<>();

        for (Register register : registerList) {
            registerData.put(register.getName(), register.getBinaryString());
        }
    }

    /**
     * For building from preferences
     *
     * @param name
     * @param data Map of register names to its properly sized binary string
     */
    public Profile(String name, Map<String, String> data) {
        this.name = name;
        this.registerData = data;
    }

    public String getName() {
        return name;
    }

    int getRegisterSize(String registerName) {
        // TODO: handle if its not there?
        return hasRegister(registerName) ? registerData.get(registerName).length() : -1;
    }

    int getRegisterData(String registerName) {
        return hasRegister(registerName) ? Integer.parseUnsignedInt(registerData.get(registerName), 2) : -1;
    }

    String getRegisterBinaryString(String registerName) {
        return hasRegister(registerName) ? registerData.get(registerName) : ""; // todo
    }

    boolean hasRegister(String registerName) {
        return registerData.containsKey(registerName);
    }

    Collection<String> getRegisterNames() {
        return registerData.keySet();
    }
}
