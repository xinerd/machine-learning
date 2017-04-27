println("hello world")

// values and variables
var d1: Double = 3.3
val d2 = 2.0
var i1: Int = 1


// boolean and comparison operators
var b1 = 1 > 2

// string
var s1 = "hello" * 5

val name = "xinerd"

val greet = s"hello ${name}"
val greet1 = s"hello $name"
val greet2 = f"hello $name"
val hello = greet slice (0, 5)

printf("a string %s, an integer %d, a float %f", "hi" , 10, 12.123)
printf("a float %1.2f", 1.235)
printf("a float %1.2f", 22.235)

// basic regex
val r1 = "term1 term2 term3"
r1 matches "term1 term2 term3"
r1 matches "term1"
r1 contains "term2"

// tuples
val my_tup1 = (1,2.2,"hello",true)
val my_tup2 = (1,2,3,(4,5))
val my_tup3 = my_tup1._3
val my_tup4 = my_tup2._4
val my_tup5 = my_tup2._4._1
