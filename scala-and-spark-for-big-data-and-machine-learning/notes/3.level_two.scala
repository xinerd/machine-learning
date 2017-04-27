// control flow

// if
var name = "xin"
if (name == "ming") {
  println("ming...")
} else if (name == "xin") {
  println("xin...")
} else {
  println("else......")
}

// for
for (item <- List(1,2,3)) {
  println("hello" + item)
}

for (i <- Array.range(0,10)) {
  if (i % 2 == 0) {
    println(s"$i is even number")
  }
}
println("set...")
for (j <- Set(4,2,1,5,2,6)) {
  println(j)
}

// while
var x = 0
while (x < 5) {
  println(s"x is currenlty $x, and still less than 5")
  x += 1
}
// import Breaks
import util.control.Breaks._
var y = 0
while (y < 10) {
  println(s"y is $y, y still less than 10, add 1 to y")
  y += 1
  if (y == 3) break
}

// functions
// def fun_name(): return_type = { // operations }
def adder(num1:Int, num2:Int): Int = {
  return num1 + num2
}

adder(4,5)

val numbers = List(1,2,3,4)
def check(nums:List[Int]): List[Int] = {
  return nums
}

println(check(numbers))
