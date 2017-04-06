package seaskyways.rxpipe

import org.junit.Test

/**
 * Created by Ahmad on 05/04 Apr/2017.
 */
class RxPipeTest {
    
    open class Person(val name: String, val age: Int)
    
    class School {
        val personPipe = pipeEndPoint<Person>(namespace = "sample_person")
        val person by personPipe
    }
    
    class WaterMelon
    
    val newPerson = Person(name = "Ahmad", age = 19)
    val school = School()
    
    @Test
    fun testBasicPiping() {
        val personPiped = newPerson.pipeStartPoint("sample_person")
        
        RxPipeManager.register<Person>(personPiped)
        RxPipeManager.register<Person>(school.personPipe)
        
        assert(newPerson == school.person)
        print("Original person : ${newPerson.name}, transferred person : ${school.person?.name}")
    }
    
}