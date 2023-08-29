package wooyoungsoo.authserver.domain.auth.entity.member;

public enum Role {
    ROLE_USER, ROLE_DOCTOR, ROLE_ADMIN;

    public static Role getRoleByName(String roleName) {
        return switch (roleName) {
            case "ROLE_USER" -> ROLE_USER;
            case "ROLE_DOCTOR" -> ROLE_DOCTOR;
            case "ROLE_ADMIN" -> ROLE_ADMIN;
            default -> ROLE_USER;
        };
    }
}
