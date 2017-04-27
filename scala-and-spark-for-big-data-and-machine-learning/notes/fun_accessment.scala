def isEven(num : Int) : Boolean = {
  return num % 2 == 0
}
def isEvenSingle(num : Int) = num % 2 == 0

println(isEven(2))
println(isEven(5))
println(isEvenSingle(2))
println(isEvenSingle(5))

def isEvenList(nums : List[Int]) : Boolean = {
  for (num <- nums) {
    if (num % 2 == 0) {
      return true
    }
  }
  return false
}

isEvenList(List(1,2,3,4))

def luckSeven(nums : List[Int]) : Int = {
  var sum = 0;
  for (num <- nums){
    if ( num == 7 ){
      sum += num
    }
    sum += num
  }
  return sum
}
println(luckSeven(List(1,2,3,4,5,6,7,8,9,10)))

// can you balance
def findBlance(nums : List[Int]) : Boolean = {
  var firstHalf = 0
  var secondHalf = nums.sum

  for (i <- Range(0, nums.length)) {
    firstHalf += nums(i);
    secondHalf -= nums(i);
    if (firstHalf == secondHalf) {
      return true
    }
  }
  return false
}
val list1 = List(1,2,3,3,3);
val list2 = List(1,2,4,2,1,2,5);
println(findBlance(list1))
println(findBlance(list2))



// palindrome
def palindromeCheck(input : String) = input == input.reverse

println(palindromeCheck("abc"))
println(palindromeCheck("abcba"))
