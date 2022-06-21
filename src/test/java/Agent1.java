import java.util.*;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Random random = new Random();

        while (true) {
            int handCount = Integer.parseInt(scanner.nextLine());

            List<String> cards = new ArrayList<>();
            for (int i = 0; i < handCount; i++) {
                String card = scanner.nextLine();
                cards.add(card);
            }

            System.out.println(cards.get(random.nextInt(cards.size())));
        }
    }
}
