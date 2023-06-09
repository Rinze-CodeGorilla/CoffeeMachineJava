package machine;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

class Ingredients {
    public int water;
    public int milk;
    public int beans;
    public int cups;
    public int money;

    public Ingredients(int water, int milk, int beans, int cups, int money) {
        this.water = water;
        this.milk = milk;
        this.beans = beans;
        this.cups = cups;
        this.money = money;
    }

    public int takeMoney() {
        var moneyToReturn = money;
        money = 0;
        return moneyToReturn;
    }

    public void subtract(Ingredients other) {
        water -= other.water;
        milk -= other.milk;
        beans -= other.beans;
        cups -= other.cups;
        money -= other.money;
    }

    public Optional<String> missing(Ingredients drink) {
        if (water < drink.water) return Optional.of("water");
        else if (milk < drink.milk) return Optional.of("milk");
        else if (beans < drink.beans) return Optional.of("coffee beans");
        else if (cups < drink.cups) return Optional.of("disposable cups");
        return Optional.empty();
    }
}

public class CoffeeMachine {
    private static final Ingredients espresso = new Ingredients(250, 0, 16, 1, -4);
    private static final Ingredients latte = new Ingredients(350, 75, 20, 1, -7);
    private static final Ingredients cappuccino = new Ingredients(200, 100, 12, 1, -6);
    private static final Ingredients ingredients = new Ingredients(400, 540, 120, 9, 550);
    private State state = State.MainMenu;

    enum State {
        MainMenu,
        Buying,
        FillingWater,
        FillingMilk,
        FillingBeans,
        FillingCups,
    }

    enum MainMenuOptions {
        buy,
        fill,
        take,
        remaining,
        exit,
    }

    enum BuyingOptions {
        espresso,
        latte,
        cappuccino,
        back,
    }

    private static final Map<String, BuyingOptions> BuyingOptionsMap = Map.of(
            "1", BuyingOptions.espresso,
            "2", BuyingOptions.latte,
            "3", BuyingOptions.cappuccino,
            "back", BuyingOptions.back
    );

    private static void buy(Ingredients drink) {
        var missing = ingredients.missing(drink);
        if (missing.isEmpty()) {
            System.out.println("I have enough resources, making you a coffee!");
            ingredients.subtract(drink);
        } else {
            System.out.printf("Sorry, not enough %s\n", missing.get());
        }
    }

    private static void take() {
        System.out.printf("I gave you $%d\n", ingredients.takeMoney());
    }

    private static void status() {
        System.out.printf("""
                The coffee machine has:
                %d ml of water
                %d ml of milk
                %d g of coffee beans
                %d disposable cups
                $%d of money
                """, ingredients.water, ingredients.milk, ingredients.beans, ingredients.cups, ingredients.money);
    }

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        final var cm = new CoffeeMachine();
        while (cm.state != null) {
            cm.renderState();
            cm.updateState(scanner.next());
        }
    }

    public void updateState(String action) {
        state = switch (state) {
            case MainMenu -> switch (MainMenuOptions.valueOf(action)) {
                case buy -> State.Buying;
                case take -> {
                    take();
                    yield State.MainMenu;
                }
                case exit -> null;
                case fill -> State.FillingWater;
                case remaining -> {
                    status();
                    yield State.MainMenu;
                }
            };
            case Buying -> {
                switch (BuyingOptionsMap.get(action)) {
                    case espresso -> buy(espresso);
                    case latte -> buy(latte);
                    case cappuccino -> buy(cappuccino);
                    case back -> {}
                }
                yield State.MainMenu;
            }
            case FillingWater -> {
                ingredients.water += Integer.parseInt(action);
                yield State.FillingMilk;
            }
            case FillingMilk -> {
                ingredients.milk += Integer.parseInt(action);
                yield State.FillingBeans;
            }
            case FillingBeans -> {
                ingredients.beans += Integer.parseInt(action);
                yield State.FillingCups;
            }
            case FillingCups -> {
                ingredients.cups += Integer.parseInt(action);
                yield State.MainMenu;
            }
        };
    }

    public void renderState() {
        switch (state) {
            case MainMenu -> System.out.println("Write action (buy, fill, take, remaining, exit):");
            case Buying -> System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
            case FillingWater -> System.out.println("Write how many ml of water you want to add:");
            case FillingMilk -> System.out.println("Write how many ml of milk you want to add:");
            case FillingBeans -> System.out.println("Write how many grams of coffee beans you want to add:");
            case FillingCups -> System.out.println("Write how many disposable cups you want to add:");
        }
    }
}
