package test;


public class ChainExecutorTest {

    @Test
        public void testChainExecution(){
            TestChainExecutor testChainExecutor = new TestChainExecutor();
            TestContext testContext = new TestContext();
            testChainExecutor.execute(testContext);

            assertEquals(2, testContext.getId());
            assertEquals(2, testContext.getCount());
        }

        @Test
        public void testFailingRollbackChainExecution(){
            TestFailingRollbackChainExecutor testFailingChainExecutor = new TestFailingRollbackChainExecutor();
            TestContext testContext = new TestContext();

            testFailingChainExecutor.execute(testContext);
            assertEquals(1, testContext.getId());
            assertEquals(3, testContext.getCount());
        }


        @Test(expected = RuntimeException.class)
        public void testFailingChainExecution(){
            FailingChainExecutor testFailingChainExecutor = new FailingChainExecutor();
            TestContext testContext = new TestContext();

            testFailingChainExecutor.execute(testContext);
        }

        private class TestContext implements Context{

            public int id = 0;
            public int count = 0;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCount() {
                return count;
            }

            public void incrementCount() {
                this.count++;
            }
        }

        private static final class TestChainExecutor extends ChainExecutor<TestContext> {

            private TestHandler1 testHandler1 = new TestHandler1();
            private TestHandler2 testHandler2 = new TestHandler2();

            public TestChainExecutor(){
                testHandler1.setNext(testHandler2);
            }

            @Override
            protected Handler<TestContext> getStart() {
                return testHandler1;
            }
        }

        private static final class TestFailingRollbackChainExecutor extends ChainExecutor<TestContext> {

            private TestHandler2 testHandler2 = new TestHandler2();
            private FailingRollbackHandler failingRollbackHandler = new FailingRollbackHandler();

            public TestFailingRollbackChainExecutor(){
                testHandler2.setNext(failingRollbackHandler);
            }

            @Override
            protected Handler<TestContext> getStart() {
                return testHandler2;
            }
        }

        private static final class FailingRollbackChainExecutor extends ChainExecutor<TestContext> {

            private TestHandler2 testHandler2 = new TestHandler2();
            private FailingRollbackHandler failingRollbackHandler = new FailingRollbackHandler();

            public FailingRollbackChainExecutor(){
                testHandler2.setNext(failingRollbackHandler);
            }

            @Override
            protected Handler<TestContext> getStart() {
                return testHandler2;
            }
        }

        private static final class FailingChainExecutor extends ChainExecutor<TestContext> {

            private TestHandler2 testHandler1 = new TestHandler2();
            private FailingHandler failingHandler = new FailingHandler();

            public FailingChainExecutor(){
                testHandler1.setNext(failingHandler);
            }

            @Override
            protected Handler<TestContext> getStart() {
                return testHandler1;
            }
        }

        private static final class TestHandler1 extends Handler<TestContext> {

            @Override
            protected void process(TestContext context) {
                context.incrementCount();
                context.setId(1);
            }
        }

        private static final class TestHandler2 extends Handler<TestContext> {

            @Override
            protected void process(TestContext context) {
                context.incrementCount();
                context.setId(2);
            }
        }

        private static final class FailingRollbackHandler extends Handler<TestContext> {

            @Override
            protected Handler<TestContext> getFailureHandler() {
                return new TestHandler1();
            }

            @Override
            protected void process(TestContext context) {
                context.incrementCount();
                throw new RuntimeException("It broke");
            }
        }

        private static final class FailingHandler extends Handler<TestContext> {

            @Override
            protected void process(TestContext context) {
                context.incrementCount();
                throw new RuntimeException("It broke");
            }
        }
}
