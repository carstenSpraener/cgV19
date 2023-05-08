#Associations

![Data Model from project restDEMO](../../restDemo/doc/img/class__model__DataModel.png)

## OneToMany

```
            nameB                nameA 
Class A      #------------------------   Class B
             1                   0..*
```

### Identification

### Result
```java
class A  {
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "nameB",
            orphanRemoval = true)
   private Set<B> nameA = new HashSet<>();

   // CollectionManagement-Methods
   public void addNameA( B value ) {
      this.nameA.add(value);
      value.setNameB(this);
   }

   public void removeNameA( B value ) {
      this.nameA.remove(value);
      value.setNameB(null);
   }
}

class B {
    @ManyToOne(fetch=FetchType.LAZY)
    private A nameB;
}
```

### Example

## ManyToMany 

### Identification

### Result
