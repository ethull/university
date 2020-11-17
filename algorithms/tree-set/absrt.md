bespoke implementation for sets of integers
    element collection
    no particular order
    no multiple occurances
    one set = anouther it same elems

    empty set
        one needed
        but different so needs own class
        {}
    singeton set
        sets with one num
    tree set
        sets with >1 num
        each set is a tree with 2 branches
            left branch even numbers
            right branch odd numbers
            for each child last bit (divide all nums by 2 and round down)

    immutable
        cant change contents after construction
        build up set?
            create empty set obj -> add() returns new set with elems
    smart constructors
        hide actual constructor (private)
            objects can be made outside the class
        public static mtd
            does constructing
            returns obj of right type
                obj doesnt have to be freshly constructed
                or use constructor of subclass
            will need to store some data in static vars
