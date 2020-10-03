package brain

import java.util.HashMap
import java.util.Collections

class Memory {
    companion object {
        val memoryMap = HashMap<MemoryType, MutableList<MemoryInformation>>()

        fun storeNewMemory(memoryType: MemoryType, memoryInformation: MemoryInformation) {
            if(!memoryMap.containsKey(memoryType)) {
                val memoryInformationList: MutableList<MemoryInformation> = ArrayList()
                memoryInformationList.add(memoryInformation)
                memoryMap.put(memoryType, memoryInformationList)
                return
            }
            val memoryInformationList: MutableList<MemoryInformation> = memoryMap[memoryType]!!
            memoryInformationList.add(memoryInformation)
        }

        fun retrieveMemory(memoryType: MemoryType): MutableList<MemoryInformation>? {
            return memoryMap.get(memoryType)
        }
    }
}
