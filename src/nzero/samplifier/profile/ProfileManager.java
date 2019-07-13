package nzero.samplifier.profile;

import nzero.samplifier.model.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileManager {
    private List<Profile> profiles;


    public ProfileManager(String profileManifestFilename) {

    }

    /**
     * Loads the data from <tt>profile</tt> into the register objects contained in the list <tt>target</tt>.
     * Also performs a consistency check to make sure the profile is compatible with the target registers.
     *
     * @param profile
     * @param destRegs
     * @throws ProfileMismatchException if the profile is incompatible with the target registers.
     */
    public void loadProfile(Profile profile, List<Register> destRegs) throws ProfileMismatchException {

        // Perform consistency check
        if (!areCompatible(profile, destRegs)) {
            throw new ProfileMismatchException();
        }

        List<Register> sourceRegs = profile.getRegisters();
        try {
            for (int i = 0; i < sourceRegs.size(); i++) { // Iterate over registers
                Register sourceReg = sourceRegs.get(i);
                Register destReg = destRegs.get(i);
                destReg.setData(sourceReg.getData());
//                for (int j = 0; j < sourceReg.getNumMappings(); j++) { // Iterate over bit maps
//                    BitMap sourceBitMap = sourceReg.getBitMaps().get(j);
//                    BitMap destBitMap = destReg.getBitMaps().get(j);
//                    destBitMap.setData(sourceBitMap.getRawData()); // Set data
//                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ProfileMismatchException(); // TODO: this is bad
        }

    }

    /**
     * Checks that profile is compatible with <tt>registers</tt>.
     *
     * @return true if the profile is compatible with the target registers
     * @implNote This check verifies the profile's registers and target's registers bit maps are sequentially the same
     * length, and depends on the List's ordering of bit maps and registers.
     */
    public boolean areCompatible(Profile profile, List<Register> registers) {
        // must have same num registers
        if (profile.getRegisters().size() != registers.size()) {
            return false;
        }

        // Iterate over registers
        for (int i = 0; i < registers.size(); i++) {
            Register a = profile.getRegisters().get(i);
            Register b = registers.get(i);

            // bit width and number of bit maps in both registers must be equal
            if (a.getBitWidth() == b.getBitWidth() && a.getBitMaps().size() == b.getBitMaps().size()) {
                // Iterate over bit maps, make sure they're all the same length
                for (int j = 0; j < a.getBitMaps().size(); j++) {
                    if (a.getBitMaps().get(j).getLength() != b.getBitMaps().get(j).getLength())
                        return false;
                }
            } else {
                return false; // registers do not have same bit width and num bit maps
            }
        }
        return true;
    }

    public List<String> getProfiles() {
        List<String> profileNames = new ArrayList<>(profiles.size());
        for (Profile profile : profiles) {
            profileNames.add(profile.getName());
        }
        return profileNames;
    }

    public void createProfile(String name, List<Register> registers) {
        Profile profile = new Profile(name, deepCopy(registers)); // TODO: check for dup names? Propogate changed back to GUI?
        profiles.add(profile);
    }

    public void removeProfile(String name) {
        Profile profile = getProfile(name);
        if (profile != null) {
            profiles.remove(profile);
        }
    }

    private Profile getProfile(String name) {
        for (Profile profile : profiles) {
            if (profile.getName().equals(name)) {
                return profile;
            }
        }
        return null;
    }

    public int numProfiles() {
        return profiles.size();
    }

    public boolean existsProfile(String name) {
        return getProfile(name) != null;
    }

    private static List<Register> deepCopy(List<Register> sourceRegs) {
        List<Register> destRegs = new ArrayList<>(sourceRegs.size());
        for (Register sourceReg : sourceRegs) {
            destRegs.add(new Register(sourceReg));
        }
        return destRegs;
    }
}
