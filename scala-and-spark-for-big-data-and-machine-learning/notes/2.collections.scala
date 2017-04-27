// List
val evens = List(4,2,6,10,8,8)
val mixed_evens = List(2, 4.0, 6, 8, 10, true)
evens(0)
mixed_evens(5)
evens.head
evens.tail
evens.size
evens.max
evens.sum
evens.sorted

evens.contains(6)
evens.drop(2)
evens.takeRight(2)
val evens1 = evens slice(3,6)

// Array
val arr = Array(1,2,3,"four")
Array.range(0,10)
Array.range(0,10,2)
Range(0,10)

// set
val s1 = Set(1,2,3,3) // immutable
val s2 = collection.mutable.Set(1,2,3,3)
s2 += 5
s2.add(8)
s2
s2.min
val my_new_set = evens.toSet

// map
val mymap = Map(("a",1),("b",2),("c",3))
mymap("a")
mymap("d") // exception
mymap get "a"
mymap get "d" // none
val mymutmap = collection.mutable.Map(("x",1),("y",2),("z",3))
mymutmap += ("m" -> 999)
mymutmap += (("m" -> 999), ("n" -> 888))
mymutmap
mymutmap.keys
mymutmap.values
