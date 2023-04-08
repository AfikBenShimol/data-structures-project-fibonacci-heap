# data-stractures-project-fibonacci-heap

This is the second programming project in data structures course. </br>
In this project we will implement a fibonacci heap using java. </br>

- [Fibonacci Heap class](#Fibonacci-Heap-Class)
    - [functions & time complexity table](#Fibonacci-Heap-functions-&-time-complexity)
- [Maintainers](#Creators-/-Maintainers)

## Fibonacci Heap Class

Fibonacci Heap include the implementation of the Heap's functionality and the methods accessible to the user.
    
### Fibonacci Heap functions & time complexity

| Function                       | Description                                                                                             | Time Complexity |
|:-------------------------------|:--------------------------------------------------------------------------------------------------------|:----------------|
| isEmpty()                      | Returns true if and only if the Heap is empty.                                                          | O(1)            |
| insert(int i)                  | Inserts to the heap new heap node with the key i and returns the new heap node.                         | O(1)            |
| deleteMin()                    | Removes the node with the minimal key from the heap.                                                    | O(log(n))       | 
| findMin()                      | Returns the node with the minimal key from the heap.                                                    | O(1)            |
| meld(FibonacciHeap heap2)      | Melds heap2 with the current heap.                                                                      | O(1)            |
| size()                         | Returns the size of the heap.                                                                           | O(1)            |
| counterRep()                   | Returns an array where the i'th element represents the num of trees in the heap which their rank is i.  | O(n)            |
| delete(HeapNode x)             | Deletes x from the heap.                                                                                | O(n)            | 
| decreaseKey(HeapNode x, int d) | Decreases the key of x by d.                                                                            | O(log(n))       |
| nonMarked()                    | Returns the number of nodes in the heap which are not marked.                                           | O(1)            |
| potential(lst)                 | Returns the current potential of the heap which we calculate by num of trees + 2 * num of marked nodes. | O(1)            |
| totalLinks()                   | Static function which returns the sum of links done while the app is running.                           | O(1)            |
| totalCuts()                    | Static function which returns the sum of cuts done while the app is running.                            | O(1)            |
| kMin(FibonacciHeap H, int k)   | Static function which returns a sorted array of the k smallest nodes in a binomial heap.                | O(k * deg(H))   |

## Creators / Maintainers

- Dor Liberman ([dorlib](https://github.com/dorlib))
- Afik Ben Shimol ([AfikBenShimol](https://github.com/AfikBenShimol))

If you have any questions or feedback, I would be glad if you will contact me via mail.

<p align="left">
  <a href="afik1200@gmail.com"> 
    <img alt="Connect via Email" src="https://img.shields.io/badge/Gmail-c14438?style=flat&logo=Gmail&logoColor=white" />
  </a>
</p>

This project was created for educational purposes, for personal and open-source use.

If you like my content or find my code useful, give it a :star:
