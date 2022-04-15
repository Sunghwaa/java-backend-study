import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Homework_1 {
    @Test
    public void concatWithDelay() {
        // ["Blenders", "Old", "Johnnie"] 와 "[Pride", "Monk", "Walker”] 를 순서대로 하나의 스트림으로 처리되는 로직 검증

        Flux<String> names1 = Flux.just("Blenders", "Old", "Johnnie")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names2 = Flux.just("Pride", "Monk", "Walker")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> names = Flux.concat(names1, names2)
                .log();

        StepVerifier.create(names)
                .expectSubscription()
                .expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
                .verifyComplete();
    }


    @Test
    public void getEvenNumbers() {
        // 1~100 까지의 자연수 중 짝수만 출력하는 로직 검증

        Flux<Integer> evenNums = Flux.range(1, 100)
                .filter(i -> i % 2 == 0)
                .log();

        StepVerifier.create(evenNums)
                .expectNextSequence(IntStream.iterate(2, i -> i <= 100, i -> i + 2).boxed().collect(Collectors.toList()))
                .verifyComplete();
    }

    @Test
    public void getStringSequentially() {
        // “hello”, “there” 를 순차적으로 publish하여 순서대로 나오는지 검증

        Flux<String> str = Flux.just("hello", "there")
                .log();

        StepVerifier.create(str)
                .expectNext("hello")
                .expectNext("there")
                .verifyComplete();
    }

    @Test
    public void nameToUpperCase() {
        /*
        * 아래와 같은 객체가 전달될 때 “JOHN”, “JACK” 등 이름이 대문자로 변환되어 출력되는 로직 검증
        * Person("John", "[john@gmail.com](mailto:john@gmail.com)", "12345678")
        * Person("Jack", "[jack@gmail.com](mailto:jack@gmail.com)", "12345678")
        * */

        Flux<Person> people = Flux.just(
                        new Person("John", "[john@gmail.com](mailto:john@gmail.com)", "12345678"),
                        new Person("Jack", "[jack@gmail.com](mailto:jack@gmail.com)", "12345678"))
                .doOnNext(p -> p.Name = p.Name.toUpperCase())
                .log();

        StepVerifier.create(people)
                .expectNextMatches(p -> p.Name.equals("JOHN"))
                .expectNextMatches(p -> p.Name.equals("JACK"))
                .expectComplete()
                .verify();
    }

    @Test
    public void concatStrings() {
        /*
        * ["Blenders", "Old", "Johnnie"] 와 "[Pride", "Monk", "Walker”]를 압축하여 스트림으로 처리 검증
        *  - 예상되는 스트림 결과값 ["Blenders Pride", "Old Monk", "Johnnie Walker”]
        * */
        Flux<String> strings1 = Flux.just("Blenders", "Old", "Johnnie");
        Flux<String> concatenatedStrings = Flux.just("Pride", "Monk", "Walker")
                .zipWith(strings1)
                .map(t -> t.getT2() + " " + t.getT1())
                .log();

        StepVerifier.create(concatenatedStrings)
                .expectNext("Blenders Pride")
                .expectNext("Old Monk")
                .expectNext("Johnnie Walker")
                .verifyComplete();
    }

    @Test
    public void longStringToUpperCase(){
        /*
        * ["google", "abc", "fb", "stackoverflow”] 의 문자열 중 5자 이상 되는 문자열만 대문자로 비동기로 치환하여 1번 반복하는 스트림으로 처리하는 로직 검증
        *  - 예상되는 스트림 결과값 ["GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW"]
        * */

        Flux<String> operatedStrings = Flux.just("google", "abc", "fb", "stackoverflow")
                .filter(s -> s.length() >= 5)
                .map(String::toUpperCase)
                .repeat(1)
                .log();

        StepVerifier.create(operatedStrings)
                .expectNextSequence(Arrays.asList("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW"))
                .verifyComplete();
    }
}

