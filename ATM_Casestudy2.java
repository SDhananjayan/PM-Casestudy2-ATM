import java.io.*;
import java.util.*;

//Interface which has the getters and setters of the essential details
interface User_details{
	void setUserName(String s);
	String getUserName();
	void setAccount_type(String s);
	String getAccount_type();
	void setAccount_No(int a);
	int getAccount_No();
	void setPIN(int a);
}

//Abstract class which defines the getters and setters and defines an abstract method and declares 
// the private variables(which can't be done in an interface)
abstract class Private_User_Details implements User_details{
	private int Account_No;
	private String Account_type;
	private String username;
	private int pin;	 
	public void setUserName(String s){
		username = s;
	}  
	public String getUserName(){
		return username;
	}
	public void setAccount_type(String s){
		Account_type = s;
	}
	public String getAccount_type(){
		return Account_type;
	}
	public void setAccount_No(int a){
		Account_No = a;
	}
	public int getAccount_No(){
		return Account_No;
	}
	public void setPIN(int a){
		pin = a;
	}
	abstract boolean invalid_AC_no();
}

//class which defines the invalid ac_no and has the account match map to check for account count violations
class User extends Private_User_Details{
	User(String c){
		setUserName(c);
	}
	Map<Integer, Integer> AccountMatch = new HashMap<Integer,Integer>();
	String[] AccountType = {"Savings", "Salary","FD","Current","Demat"};
	//Checks if the first digit of the account no matches with the accounttype
	public boolean invalid_AC_no(){
		for(int i=0;i<5;i++){
			if(getAccount_type()==AccountType[i]){
				if(getAccount_No()/10000==i+1){
					return false;
				}
				else{
					return true;
				}
			}
		}
		return false;
	}
}

public class CASESTUDY2	{
	Scanner input = new Scanner(System.in);
	static CASESTUDY2 o = new CASESTUDY2();
	//Maps from account number to PIN and Balance, and a list of blocked accounts
	Map<Integer, Integer> PINmatch = new HashMap<Integer,Integer>();
	ArrayList<Integer> BlockedAccount = new ArrayList<Integer>(); 
	Map<Integer, Integer> Balance = new HashMap<Integer,Integer>();
	boolean[] AccountCount = new boolean[5];
	String UName = "";
	int type,ac_no,pin,mmenu;
	//Initializing ATM with 1 Lakh rupees(Array denotes freq. of 10,20,50,100,200,500,1k,2k notes respectively)
	int CashLeft[] = {50,25,20,30,25,30,25,25};

	//Set the balance of an account to be Rs.10,000
	void Initialize_Balance(User u){
		if(PINmatch.containsKey(u.getAccount_No())){
			if(!Balance.containsKey(u.getAccount_No())){
				Balance.put(u.getAccount_No(), 10000);
			}
		}
	}
	public void welcome(){
		System.out.println("\n\nWelcome to XXX ATM of YYY Bank\n");
		System.out.println("Please enter your username:");
		UName = input.next();
	}
	void Menu_AccountType(){
		System.out.println("Please choose the type of account you want to access from the given menu:");
		System.out.println("1 : Savings");
		System.out.println("2 : Salary");
		System.out.println("3 : FD");
		System.out.println("4 : Current");
		System.out.println("5 : Demat");
		type = input.nextInt();
		assert(type>0 && type<6);
	}
	void Menu_AccountNumber(){
		System.out.print("Please enter your 5 digit account number: ");
		ac_no = input.nextInt();
		assert(ac_no>9999 && ac_no<100000);
	}

	//Check if the user has multiple accounts of the same type using AccountMatch map
	boolean AccountCountViolation(User u){
		if(AccountCount[type-1]==true){
			if(u.AccountMatch.containsKey(type-1) && u.AccountMatch.get(type-1)!=ac_no){
				System.out.println("You are trying to use multiple accounts of the same type, which is prohibited by the Company.");
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	//Alerting the user of the number of tries and validity of PIN and blocking the account if there are 3 wrong PIN inputs
	void Menu_PIN(int b, User u){
		if(b<3 && BlockedAccount.indexOf(u.getAccount_No())==-1){
			System.out.println("You have " + (3-b) +" tries left to enter the correct PIN." );
			System.out.print("Please enter your 5 digit PIN: ");
			pin = input.nextInt();
			assert(pin>9999 && pin<100000);
			if(pin/10000==0 | pin/100000!=0){
				System.out.println("The PIN you entered did not have 5 DIGITS");
				Menu_PIN(b+1,u);
			}
		}
		else if(b==3){
			System.out.println("\nSince you have made 3 incorrect attempts to enter the correct PIN, your account has been blocked");
			o.BlockedAccount.add(u.getAccount_No());
			System.exit(0);
		}
		else{
			System.out.println("Your account has been blocked.Please contact your nearest branch for further details.");
			System.exit(0);
		}
	}

	//Checking if the PIN is correct using the PINmatch map 
	boolean pinmismatch(User u){
		if(PINmatch.containsKey(u.getAccount_No()) && PINmatch.get(u.getAccount_No())!=pin){
			System.out.println("Your Account Number and PIN do not match.");
			return true;
		}
		else{
			PINmatch.put(u.getAccount_No(), pin);
			u.setPIN(pin);
			return false;
		}
	}
	void Display_Balance(User u){
		System.out.println("Your Balance is : Rs. " + Balance.get(u.getAccount_No()));
	}
	void Ask_Notes(int i){
		System.out.println("Please enter the number of " + i + " Rs. Notes you want to deposit: ");
	}

	//Asking the number of Rs.X notes and calculating the amount and adding it to the account
	void Deposit(User u){
		int amount = 0;
		int i = 10;
		o.Ask_Notes(10);
		int local = input.nextInt();
		CashLeft[0]+=local;
		amount+=10*local;
		i=20;
		int count = 1;
		while(i<2001){
			o.Ask_Notes(i);
			local = input.nextInt();
			CashLeft[count]+=local;
			amount+=i*local;
			if(i==20 | i==200){
				i*=5;
				i/=2;
			}
			else{
				i*=2;
			}
			count++;
		}
		Balance.put(u.getAccount_No(), Balance.get(u.getAccount_No())+amount);
	}
	void InsufficientFunds(){
		System.out.println("The ATM does not have sufficient funds to process your withdrawal request. We regret any inconvenience this might have caused");
	}
	void InsufficientBalance(){
		System.out.println("Your Account does not have sufficient balance to process your withdrawal request.");
	}
	void WrongDenomoinations(){
		System.out.println("The ATM does not have the denominations required to facilitate your withdrawal request. We regret any inconvenience this might have caused");
	}
	int minimum(int a,int b){
		return (a<b?a:b);
	}

	//Finding the amount of cash left in the ATM.
	int AvailableMoney(){
		int sum = 0;
		int value = 10;
		for(int i=0;i<8;i++){
			sum+=CashLeft[i]*value;
			if(value==20 | value==200){
				value*=5;
				value/=2;
			}
			else{
				value*=2;
			}
		}
		return sum;
	}

	//Checking if requested withdrawal is possible, and if so, executing the withdrawal
	void Withdraw(User u){
		System.out.println("Please enter the amount you want to withdraw");
		int with = input.nextInt();
		if(with%10!=0){
			o.WrongDenomoinations();
		}
		else if(with>Balance.get(u.getAccount_No())){
			o.InsufficientBalance();
		}
		else if(with>o.AvailableMoney()){
			o.InsufficientFunds();
		}
		else{
			int index = 7;
			int value = 2000;
			Balance.put(u.getAccount_No(), Balance.get(u.getAccount_No()) - with);
			while(with>0 && index>=0){
				int tem =minimum(with/value, CashLeft[index]);
				with-= (tem)*(value);
				CashLeft[index]-=tem;
				index--;
				if(value==500 | value==50){
					value*=2;
					value/=5;
				}
				else{
					value/=2;
				}
			}
			System.out.println("Your Withdrawal was successful");
		}
	}
	int Menu_Main(User u){
		System.out.println("Please choose the action you want to make from the given menu: ");
		System.out.println("1 : View Account Balance");
		System.out.println("2 : Deposit Cash");
		System.out.println("3 : Withdraw Cash");
		System.out.println("4 : Log out from this Account");
		System.out.println("5 : Log out from this username");
		System.out.println("6 : Exit the Interface");
		mmenu = input.nextInt();
		assert(mmenu>0 && mmenu<7);
		return mmenu;
	}
	public static void main(String[] args){
		while(true){
			o.welcome();
			User u = new User(o.UName);
			//Defining user_x so that one user can log out and the next user can log in
			user_x:
			while(true){
				o.Menu_AccountType();
				o.Menu_AccountNumber();
				u.setAccount_type(u.AccountType[o.type-1]);
				u.setAccount_No(o.ac_no);
				while(u.invalid_AC_no() | o.AccountCountViolation(u)){
					if(u.invalid_AC_no()){
						System.out.println("Your account number and account type do not match");
						u.setAccount_No(0);
					}
					else{
						u.setAccount_type("");
						o.Menu_AccountType();
						u.setAccount_type(u.AccountType[o.type-1]);
					}
					o.Menu_AccountNumber();
					u.setAccount_No(o.ac_no);
				}
				u.AccountMatch.put(o.type-1,o.ac_no);
				o.AccountCount[o.type-1] = true;
				u.setAccount_type(u.AccountType[o.type-1]);
				int count = 0;
				o.Menu_PIN(count,u);
				if(o.BlockedAccount.indexOf(u.getAccount_No())!=-1){
					System.exit(0);
				}
				count++;
				while(o.pinmismatch(u)){
					o.Menu_PIN(count,u);
					if(count==3){
						break;
					}
					count++;
				}
				o.pinmismatch(u);
				o.Initialize_Balance(u);
				int switcher = o.Menu_Main(u);
				while(switcher!=4){
					switch(switcher){
						case 1:
							o.Display_Balance(u);
							break;
						case 2:
							o.Deposit(u);
							break;
						case 3:
							o.Withdraw(u);
							break;
						case 5:
							//Breaks the two loops so that a new user can use the ATM.
							break user_x;
						case 6:
							//Terminates the program
							System.exit(0);
					}
					switcher = o.Menu_Main(u);
				}
			}
		}
	}
}
