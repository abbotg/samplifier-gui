package nzero.samplifier.profile;

import nzero.samplifier.model.Register;

import java.util.List;
import java.util.UUID;


/**
 * Stores data for the entire set of registers
 */
class Profile {
    private String name;
    private List<Register> registers;

    Profile(String name, List<Register> registers) {
        this.name = name;
        this.registers = registers;
    }

    public String getName() {
        return name;
    }

    public List<Register> getRegisters() {
        return registers;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        return name.equals(profile.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
