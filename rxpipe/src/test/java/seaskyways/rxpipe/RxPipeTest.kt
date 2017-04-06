package seaskyways.rxpipe

import org.junit.*
import org.junit.Assert.*

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
class RxPipeTest {
    
    data class Person(val name: String, val age: Int)
    
    class School(val schoolName: String = "") {
        val person by quickEndPipe<Person>()
    }
    
    
    /** Basic piping, startPoint is defined before the endPoint
     *  value is delivered from startPoint to endPoint and then both points are destroyed
     * */
    @Test
    fun testBasicPiping() {
        println("\n\nBasic Piping\n\n")
        val newPerson = Person(name = "Ahmad", age = 19) // variable to pipe through to school object
        
        newPerson.quickStartPipe(School::person) // setup a quickPipe
        
        val school = School() // at this point newPerson should go straight to the person inside the School instance
        
        assert(newPerson == school.person)
        print("Original person : $newPerson, transferred person : ${school.person}\n")
        print("hashCode comparison : ${newPerson.hashCode()} == ${school.person?.hashCode()} => ${newPerson.hashCode() == school.person?.hashCode()}\n")
    }
    
    @Test
    fun deferredPiping() {
        println("\n\nDeferred Piping\n\n")
        
        val school = School()
        assert(school.person == null)
        
        val newPerson = Person(name = "Ahmad", age = 19)
        newPerson.quickStartPipe(School::person)
        
        assert(school.person == newPerson)
        print("Original person : $newPerson, transferred person : ${school.person}\n")
        print("hashCode comparison : ${newPerson.hashCode()} == ${school.person?.hashCode()} => ${newPerson.hashCode() == school.person?.hashCode()}\n")
    }
    
    @Test
    fun deferredPipingMultipleReceivers() {
        println("\n\nDeferred Multiple Piping\n\n")
        
        val schools = Array(3) { School("School ${it + 1}") }
        
        val newPerson = Person(name = "Ahmad Al-Sharif", age = 19)
        
        newPerson.quickStartPipe(School::person, initialVolatility = 3)
        
        schools.forEach { school ->
            println("\n${school.schoolName}\n")
            assert(school.person == newPerson)
            print("Original person : $newPerson, transferred person : ${school.person}\n")
            print("hashCode comparison : ${newPerson.hashCode()} == ${school.person?.hashCode()} => ${newPerson.hashCode() == school.person?.hashCode()}\n")
        }
    }
    
    @Test
    fun testPipeVolatility() {
        println("\n\nVolatility Test\n\n")
        
        val school1 = School("School 1")
        val school2 = School("School 2")
        
        val newPerson = Person(name = "Ahmad Al-Sharif", age = 19)
        
        newPerson.quickStartPipe(School::person, initialVolatility = 1)
    
        println(school1.schoolName)
        println("Original person : $newPerson, transferred person : ${school1.person}\n")
        println("hashCode comparison : ${newPerson.hashCode()} == ${school1.person?.hashCode()} => ${newPerson.hashCode() == school1.person?.hashCode()}\n")
    
        assertEquals("School 1 only should receive newPerson", school1.person, newPerson)
        
        println(school2.schoolName)
        println("Original person : $newPerson, transferred person : ${school2.person}\n")
        println("hashCode comparison : ${newPerson.hashCode()} == ${school2.person?.hashCode()} => ${newPerson.hashCode() == school2.person?.hashCode()}\n")
    
        assertNotEquals("School 2 shouldn't receive newPerson", school2.person, newPerson)
    }
}