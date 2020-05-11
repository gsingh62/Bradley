import java.util.HashMap

class Memory {
    companion object {
        val memoryMap = HashMap<MemoryType, MemoryInformation>()

        fun store(memoryType: MemoryType) {
            memoryMap.putIfAbsent(memoryType, MemoryInformation())
        }

        fun updateMemoryWithInformation(memoryType: MemoryType, memoryInformation: MemoryInformation) {
            if(memoryExists(memoryType)) {
                val memoryInformation = memoryMap.get(memoryType).add(memoryInformation)
            }
        }

        fun retrieveMemory(memoryType: MemoryType): MemoryType {
            if (!memoryExists(memoryType)) {
                store(memoryType)
            }
            return memoryType
        }

        fun memoryExists(memoryType: MemoryType): Boolean {
            return memoryMap.containsKey(memoryType)
        }
    }
}
