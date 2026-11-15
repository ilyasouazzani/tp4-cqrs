package ma.ilyas;
import jakarta.persistence.*;import java.math.BigDecimal;
@Entity public class Account {@Id public String id;public BigDecimal balance;public String status;protected Account(){}public Account(String i,BigDecimal b,String s){id=i;balance=b;status=s;}}
