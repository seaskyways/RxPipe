package seaskyways.rxpipe

import org.junit.Test

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
class RxPipeTest {
    
    data class Person(val name: String, val age: Int)
    
    class School {
        val person by quickEndPipe<Person>()
    }
    
    
    @Test
    fun testBasicPiping() {
        val newPerson = Person(name = "Ahmad", age = 19)
        newPerson.quickStartPipe(School::person)
        
        val school = School()
        
        assert(newPerson == school.person)
        print("Original person : $newPerson, transferred person : ${school.person}\n")
        print("hashCode comparison : ${newPerson.hashCode()} == ${school.person?.hashCode()} => ${newPerson.hashCode() == school.person?.hashCode()}\n")
    }
    
}