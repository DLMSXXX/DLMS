package DLMSReplica.dlmsReplicabin;

public class Customer {

	private String accountNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String password;
	private String creditLimit = "1000";

	public Customer(String accountNumber, String firstName, String lastName, String email, String phoneNumber,
			String password) {
		super();
		this.accountNumber = accountNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreditLimit() {
		return creditLimit;
	}

	@Override
	public String toString() {
		return "Customer [accountNumber=" + accountNumber + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", phoneNumber=" + phoneNumber + ", password=" + password + ", creditLimit="
				+ creditLimit + "]";
	}

	

}
