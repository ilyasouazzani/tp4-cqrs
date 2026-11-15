package ma.ilyas;
import jakarta.persistence.*;import java.math.BigDecimal;
@Entity public class AccountOperation {@Id public String id;public String accountId;public String type;public BigDecimal amount;public String date;protected AccountOperation(){}public AccountOperation(String a,String t,BigDecimal x){id=java.util.UUID.randomUUID().toString();accountId=a;type=t;amount=x;date=java.time.Instant.now().toString();}}
