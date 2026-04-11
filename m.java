import java.util.Random;

public class m {

    public static void main(String[] args) {
        Simulation simulation = new Simulation(
                40, // 總回合數
                10, // 每隔幾回合環境改變一次
                3, // 選項數量
                0.85, // 記憶保留率（越高越長記憶，越低越短記憶）
                0.25 // 學習率（越高越快學新環境）
        );

        simulation.run();
    }
}

class Simulation {
    private int totalRounds;
    private int changeInterval;
    private int optionCount;
    private double memoryRetention;
    private double learningRate;

    public Simulation(int totalRounds, int changeInterval, int optionCount,
            double memoryRetention, double learningRate) {
        this.totalRounds = totalRounds;
        this.changeInterval = changeInterval;
        this.optionCount = optionCount;
        this.memoryRetention = memoryRetention;
        this.learningRate = learningRate;
    }

    public void run() {
        Agent agent = new Agent(optionCount, memoryRetention, learningRate);
        Environment env = new Environment(optionCount);

        int currentBest = env.getBestOption();
        int previousBest = -1;

        int environmentChangeCount = 0;
        int totalAdaptTime = 0;
        int totalOldBestChosenAfterChange = 0;
        int totalStepsAfterChange = 0;
        int rememberedOldBestCount = 0;

        boolean waitingForAdapt = false;
        int roundsSinceChange = 0;
        boolean firstChoiceAfterChangeChecked = false;

        System.out.println("=== Learning Simulation Start ===");
        System.out.println("初始最佳選擇 = " + currentBest);
        System.out.println();

        for (int round = 1; round <= totalRounds; round++) {

            // 環境改變
            if (round > 1 && (round - 1) % changeInterval == 0) {
                previousBest = currentBest;
                currentBest = env.changeEnvironment();

                environmentChangeCount++;
                waitingForAdapt = true;
                roundsSinceChange = 0;
                firstChoiceAfterChangeChecked = false;

                System.out.println("----- 環境改變於 round " + round + " -----");
                System.out.println("舊最佳選擇 = " + previousBest + "，新最佳選擇 = " + currentBest);
            }

            int choice = agent.choose();
            double reward = env.getReward(choice);
            agent.learn(choice, reward);

            System.out.printf("Round %3d | 選擇: %d | 獎勵: %.1f%n", round, choice, reward);

            // 若環境改變後，開始統計
            if (waitingForAdapt) {
                roundsSinceChange++;
                totalStepsAfterChange++;

                // Gratitude score:
                // 環境剛變後，第一次選擇是否還記得舊最佳選擇
                if (!firstChoiceAfterChangeChecked) {
                    if (choice == previousBest) {
                        rememberedOldBestCount++;
                    }
                    firstChoiceAfterChangeChecked = true;
                }

                // Stubborn score:
                // 環境改變後，仍持續選擇舊最佳選擇的比例
                if (choice == previousBest) {
                    totalOldBestChosenAfterChange++;
                }

                // Adapt time:
                // 第一次成功選到新最佳選擇所花的時間
                if (choice == currentBest) {
                    totalAdaptTime += roundsSinceChange;
                    waitingForAdapt = false;
                    System.out.println(">>> 已適應新環境，Adapt time = " + roundsSinceChange);
                }
            }
        }

        System.out.println();
        System.out.println("=== Simulation Result ===");

        double adaptTimeAverage = environmentChangeCount == 0 ? 0.0
                : (double) totalAdaptTime / environmentChangeCount;

        double gratitudeScore = environmentChangeCount == 0 ? 0.0
                : (double) rememberedOldBestCount / environmentChangeCount;

        double stubbornScore = totalStepsAfterChange == 0 ? 0.0
                : (double) totalOldBestChosenAfterChange / totalStepsAfterChange;

        System.out.printf("Adapt time 平均值 = %.2f%n", adaptTimeAverage);
        System.out.printf("Gratitude score = %.2f%n", gratitudeScore);
        System.out.printf("Stubborn score = %.2f%n", stubbornScore);

        System.out.println();
        System.out.println("=== 指標說明 ===");
        System.out.println("Adapt time：環境改變後，幾回合後才重新選到新的最佳選擇。");
        System.out.println("Gratitude score：環境改變剛發生時，是否還記得舊的好選擇。");
        System.out.println("Stubborn score：環境改變後，仍一直偏向選舊最佳選擇的比例。");
    }
}

class Agent {
    private double[] values;
    private double memoryRetention;
    private double learningRate;
    private Random random = new Random();

    public Agent(int optionCount, double memoryRetention, double learningRate) {
        this.values = new double[optionCount];
        this.memoryRetention = memoryRetention;
        this.learningRate = learningRate;
    }

    public int choose() {
        // epsilon-greedy：大部分時間選目前認為最好的，小部分隨機探索
        double epsilon = 0.1;
        if (random.nextDouble() < epsilon) {
            return random.nextInt(values.length);
        }

        int bestIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > values[bestIndex]) {
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public void learn(int choice, double reward) {
        // 記憶衰退：所有經驗都慢慢淡化
        for (int i = 0; i < values.length; i++) {
            values[i] *= memoryRetention;
        }

        // 更新目前選擇的經驗值
        values[choice] = values[choice] + learningRate * (reward - values[choice]);
    }
}

class Environment {
    private int optionCount;
    private int bestOption;
    private Random random = new Random();

    public Environment(int optionCount) {
        this.optionCount = optionCount;
        this.bestOption = random.nextInt(optionCount);
    }

    public int getBestOption() {
        return bestOption;
    }

    public int changeEnvironment() {
        int newBest;
        do {
            newBest = random.nextInt(optionCount);
        } while (newBest == bestOption);

        bestOption = newBest;
        return bestOption;
    }

    public double getReward(int choice) {
        // 選到最佳選擇給高分，其他給低分
        if (choice == bestOption) {
            return 10.0;
        } else {
            return 2.0;
        }
    }
}