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

            String lastDiscardedCard = scanner.nextLine();

            System.err.println("Hand: " + cards);
            System.err.println("Last discarded card: " + lastDiscardedCard);

            System.out.println(cards.get(random.nextInt(cards.size())));
        }
    }
}
