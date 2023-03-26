package Service;

import DAO.AccountDAO;
import Model.Account;

import java.util.List;

public class AccountService {
    private AccountDAO accountDao;

    public AccountService(){

        accountDao = new AccountDAO();
    }
    public Account addUser(Account user) {
        return accountDao.insertAccount(user);
    }

    public List<Account> getAccounts(){
        return accountDao.getAllAccounts();
    }

    public Account login(Account account){
        return accountDao.login(account.username, account.password);
    }
    public String getUsername(String username){
        return accountDao.getUserName(username);
    }

    public Account getAccount(int id){
        return accountDao.getAccountById(id);
    }

}
