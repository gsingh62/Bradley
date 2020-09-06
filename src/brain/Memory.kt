package brain

import java.util.HashMap
import java.util.Collections

class Memory {
    companion object {
        val memoryMap = HashMap<MemoryType, MutableList<MemoryInformation>>()

        fun storeNewMemory(memoryType: MemoryType) {
            val memoryInformationList: MutableList<MemoryInformation> = Collections.emptyList<MemoryInformation>()
            memoryMap.putIfAbsent(memoryType, memoryInformationList)
        }

        fun updateMemoryWithInformation(memoryType: MemoryType, memoryInformation: MemoryInformation) {
            if(!memoryMap.containsKey(memoryType)) {
                storeNewMemory(memoryType)
            }
            val memoryInformationList: MutableList<MemoryInformation> = memoryMap.get(memoryType)!!
            memoryInformationList.add(memoryInformation)
        }

        fun retrieveMemory(memoryType: MemoryType): MutableList<MemoryInformation>? {
            return memoryMap.get(memoryType)
        }
    }
}
