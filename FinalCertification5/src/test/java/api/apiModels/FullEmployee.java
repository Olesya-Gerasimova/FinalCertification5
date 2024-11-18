package api.apiModels;

public record FullEmployee(int id, String firstName, String lastName, String middleName, int companyId, String email, String url, String phone, boolean isActive) {
}
