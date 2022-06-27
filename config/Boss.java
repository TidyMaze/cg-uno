import java.util.*;

public class Boss {
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

            int validActionCount = Integer.parseInt(scanner.nextLine());

            List<String> validActions = new ArrayList<>();
            for (int i = 0; i < validActionCount; i++) {
                String validAction = scanner.nextLine();
                validActions.add(validAction);
            }

            String lastDiscardedCard = scanner.nextLine();

            System.err.println("Hand: " + cards);
            System.err.println("Valid actions: " + validActions);
            System.err.println("Last discarded card: " + lastDiscardedCard);

            if (validActions.isEmpty()) {
                System.out.println("CANNOT PLAY!");
            } else {
                System.out.println(validActions.get(random.nextInt(validActions.size())));
            }
        }
    }
}
