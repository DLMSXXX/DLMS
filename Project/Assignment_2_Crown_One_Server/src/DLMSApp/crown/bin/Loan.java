package DLMSApp.crown.bin;

public class Loan {

	public Loan(String loanId, String accountNumber, String loanAmt, String dueDate) {
		super();
		this.loanId = loanId;
		this.accountNumber = accountNumber;
		this.loanAmt = loanAmt;
		this.dueDate = dueDate;
	}

	private String loanId;
	private String accountNumber;
	private String loanAmt;
	private String dueDate;

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getLoanAmt() {
		return loanAmt;
	}

	public void setLoanAmt(String loanAmt) {
		this.loanAmt = loanAmt;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public String toString() {
		return "Loan [loanId=" + loanId + ", accountNumber=" + accountNumber + ", loanAmt=" + loanAmt + ", dueDate="
				+ dueDate + "]";
	}

}
