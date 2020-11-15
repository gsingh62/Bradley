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
        Assert.assertEquals("Animal took 1 life unit",  memory[0].responseToExpect)
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
        Assert.assertEquals("Animal took 1 life unit",  memory[0].responseToExpect)
    }

}