package nzero.samplifier.profile;

import nzero.samplifier.SamplifierGUI;
import nzero.samplifier.model.Register;
import nzero.samplifier.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class ProfileManager {
    private List<Profile> profiles;
    private Profile defaultProfile;


    public ProfileManager() {
        profiles = buildFromPreferences();
    }

    private static List<Profile> buildFromPreferences() {
        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/profiles");
        String[] profileNames;
        try {
            profileNames = preferences.keys();
        } catch (BackingStoreException e) {
            System.err.println("Error getting preferences: " + e.getMessage());
            profileNames = new String[]{};
        }

        List<Profile> profiles = new ArrayList<>(profileNames.length);

        for (String profileName : profileNames) {
            String data = preferences.get(profileName, "");
            String[] regs = data.split(";");
            Map<String, String> profileMap = Arrays.stream(regs)
                    .map(reg -> reg.split(":"))
                    .collect(Collectors.toMap(split -> split[0], split -> split[1], (a, b) -> b, LinkedHashMap::new));
            profiles.add(new Profile(profileName, profileMap));
        }
        return profiles;
    }

    private static void saveToPreferences(Profile profile) {
        String key = profile.getName();
        String value = profile.getRegisterNames().stream().map(registerName -> registerName + ':' + profile.getRegisterBinaryString(registerName) + ';').collect(Collectors.joining());

//        String key64 = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
//        String value64 = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/profiles");
        preferences.put(key, value); // TODO: handle max length before this
        System.out.println(key);
        System.out.println(value);
    }

    /**
     * Loads the data from <tt>profile</tt> into the register objects contained in the list <tt>target</tt>.
     * Also performs a consistency check to make sure the profile is compatible with the target registers.
     *
     * @param profile
     * @param destRegs
     * @throws ProfileMismatchException if the profile is incompatible with the target registers.
     */
    public void loadProfile(String profileName, List<Register> destRegs) throws ProfileMismatchException {

        if (!existsProfile(profileName)) {
            throw new ProfileMismatchException();
        }
        Profile profile = getProfile(profileName);

        // Perform consistency check
        if (!areCompatible(profile, destRegs)) {
            throw new ProfileMismatchException();
        }

        for (Register destReg : destRegs) {
            destReg.setData(profile.getRegisterData(destReg.getName()));
        }
    }

    /**
     * Checks that profile is compatible with <tt>registers</tt>.
     *
     * @return true if the profile is compatible with the target registers
     * @implNote This check verifies the profile's registers and target's registers bit maps are sequentially the same
     * length, and depends on the List's ordering of bit maps and registers.
     */
    // TODO; registers are identified by name and bit width, change to address and bit width?
    public boolean areCompatible(Profile profile, List<Register> registers) {
        return registers.stream().allMatch(register -> profile.hasRegister(register.getName())
                && profile.getRegisterSize(register.getName()) == register.getBitWidth());
    }

    public List<String> getProfiles() {
        return profiles.stream().map(Profile::getName).collect(Collectors.toCollection(() -> new ArrayList<>(profiles.size())));
    }

    /**
     * Creates profile, replaces it if it already exists
     * @param name
     * @param registers
     */
    public void createProfile(String name, List<Register> registers) {
        if (existsProfile(name)) {
            removeProfile(name);
        }
        Profile profile = new Profile(name, registers); // TODO: check for dup names? Propogate changed back to GUI?
        profiles.add(profile);
        saveToPreferences(profile);
    }

    private void createProfile(String name, Map<String, String> data) {
        if (existsProfile(name)) {
            removeProfile(name);
        }
        Profile profile = new Profile(name, data); // TODO: check for dup names? Propogate changed back to GUI?
        profiles.add(profile);
        saveToPreferences(profile);
    }

    public void removeProfile(String name) {
        Profile profile = getProfile(name);
        if (profile != null) {
            profiles.remove(profile);
        }
    }

    private Profile getProfile(String name) {
        return profiles.stream().filter(profile -> profile.getName().equals(name)).findFirst().orElse(null);
    }

    public int numProfiles() {
        return profiles.size();
    }

    public boolean existsProfile(String name) {
        return getProfile(name) != null;
    }

    private static List<Register> deepCopy(List<Register> sourceRegs) {
        return sourceRegs.stream().map(Register::new).collect(Collectors.toCollection(() -> new ArrayList<>(sourceRegs.size())));
    }

    public void setDefaultProfile(String name) {
        //TODO: check if it exists?
        defaultProfile = getProfile(name);
        Preferences preferences = Preferences.userRoot().node("nzero/samplifier/profiles/default");
        preferences.put("defaultProfileName", name);
    }

    public Optional<String> getDefaultProfile() {
        return Optional.ofNullable(defaultProfile == null ? null : defaultProfile.getName());
    }

    public void serialize(String profileName, String path) throws IOException {
        if (!existsProfile(profileName)) {
            return; // TODO: better return
        }
        Profile profile = getProfile(profileName);
        JSONObject rootObject = new JSONObject();
        rootObject.put("samplifier-gui-version", SamplifierGUI.SAMPLIFIER_GUI_VERSION);
        rootObject.put("register-map-name", SamplifierGUI.getMapName());
        rootObject.put("profile-name", profile.getName());

        JSONArray registerArray = new JSONArray();
        for (Map.Entry<String,String> entry : profile.getRegisterDataMap().entrySet()) {
            JSONObject registerObject = new JSONObject();
            registerObject.put("name", entry.getKey());
            registerObject.put("data", entry.getValue());
            registerArray.put(registerObject);
        }
        rootObject.put("registers", registerArray);

        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(rootObject.toString(2));
        }
    }

    public String deserialize(String path) throws ProfileSerializeException {
        Path file = Paths.get(path);
        if (!Files.exists(file)) {
            throw new ProfileSerializeException(String.format("file \"%s\" does not exist", path));
        }
        String content;
        try {
            content = FileUtils.readFile(path);
        } catch (Exception e) {
            throw new ProfileSerializeException(e.getMessage());
        }
        try {
            Map<String, String> map = new LinkedHashMap<>();

            JSONObject rootObject = new JSONObject(content);
            JSONArray registersArray = rootObject.getJSONArray("registers");
            for (int i = 0; i < registersArray.length(); i++) {
                JSONObject registerObject = (JSONObject) registersArray.get(i);
                map.put(registerObject.getString("name"), registerObject.getString("data"));
            }
            String name = rootObject.getString("profile-name");
            createProfile(name, map);
            return name;

        } catch (JSONException e) {
            throw new ProfileSerializeException(String.format("file is malformed (%s)", e.getMessage()));
        }

    }

}
