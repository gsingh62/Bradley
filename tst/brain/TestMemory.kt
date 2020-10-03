package brain

import org.junit.Assert
import org.junit.Test

class TestMemory {

    @Test
    fun storesNewMemory() {
        // prepare
        val memoryType = MemoryType("animalFound", "Animal was found at coordinate 1 2")
        val memoryInformation = MemoryInformation("investigatedAnimal", "Animal took 1 life unit")

        // execute
        Memory.storeNewMemory(memoryType, memoryInformation)

        // verify
        val memory: MutableList<MemoryInformation>? = Memory.memoryMap[memoryType]
        Assert.assertNotNull(memory)
        Assert.assertEquals("investigatedAnimal",  memory!![0].actionToTake)
        Assert.assertEquals("Animal took 1 life unit",  memory!![0].responseToExpect)
    }

    @Test
    fun storesSecondMemory() {
        // prepare
        val memoryType = MemoryType("animalFound", "Animal was found at coordinate 1 2")
        val memoryInformation = MemoryInformation("investigatedAnimal", "Animal took 1 life unit")
        val memoryInformationTwo = MemoryInformation("avoidAnimal", "Life unit was not compromised")
        Memory.storeNewMemory(memoryType, memoryInformation)

        // execute
        Memory.storeNewMemory(memoryType, memoryInformationTwo)

        // verify
        val memory: MutableList<MemoryInformation>? = Memory.memoryMap[memoryType]
        Assert.assertNotNull(memory)
        Assert.assertEquals("investigatedAnimal",  memory!![0].actionToTake)
        Assert.assertEquals("Animal took 1 life unit",  memory!![0].responseToExpect)
        Assert.assertEquals("avoidAnimal",  memory!![1].actionToTake)
        Assert.assertEquals("Life unit was not compromised",  memory!![1].responseToExpect)
    }

    @Test
    fun retrievesMemory() {
        // prepare
        val memoryType = MemoryType("animalFound", "Animal was found at coordinate 1 2")
        val memoryInformation = MemoryInformation("investigatedAnimal", "Animal took 1 life unit")
        Memory.storeNewMemory(memoryType, memoryInformation)

        // execute
        val memory: MutableList<MemoryInformation>? = Memory.retrieveMemory(MemoryType("animalFound", "Animal was found at coordinate 1 2"))

        // verify
        Assert.assertNotNull(memory)
        Assert.assertEquals("investigatedAnimal",  memory!![0].actionToTake)
        Assert.assertEquals("Animal took 1 life unit",  memory!![0].responseToExpect)
    }
}