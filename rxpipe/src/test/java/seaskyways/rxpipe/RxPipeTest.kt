package seaskyways.rxpipe

import org.junit.Test

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
}