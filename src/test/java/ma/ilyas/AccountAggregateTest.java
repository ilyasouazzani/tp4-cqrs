package ma.ilyas;
import org.junit.jupiter.api.Test;import org.axonframework.test.aggregate.AggregateTestFixture;import java.math.BigDecimal;import static ma.ilyas.Messages.*;
class AccountAggregateTest {
@Test void credit(){new AggregateTestFixture<>(AccountAggregate.class).given(new Created("a",new BigDecimal("100")),new Activated("a")).when(new Credit("a",new BigDecimal("20"))).expectSuccessfulHandlerExecution().expectEvents(new Credited("a",new BigDecimal("20")));}
@Test void insufficient(){new AggregateTestFixture<>(AccountAggregate.class).given(new Created("a",BigDecimal.TEN),new Activated("a")).when(new Debit("a",new BigDecimal("20"))).expectException(IllegalStateException.class);}
@Test void inactive(){new AggregateTestFixture<>(AccountAggregate.class).given(new Created("a",BigDecimal.TEN)).when(new Credit("a",BigDecimal.ONE)).expectException(IllegalStateException.class);}
@Test void negative(){new AggregateTestFixture<>(AccountAggregate.class).givenNoPriorActivity().when(new Create("a",new BigDecimal("-1"))).expectException(IllegalArgumentException.class);}}
