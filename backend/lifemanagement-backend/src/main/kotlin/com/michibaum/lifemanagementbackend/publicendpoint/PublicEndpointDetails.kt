package com.michibaum.lifemanagementbackend.publicendpoint

class PublicEndpointDetails(
    rawPaths: List<String>,
    private val character: Character,
    private val numerus: Numerus
) {

    val antPaths: List<String> = rawPaths.map { rawPath: String -> generateAntPath(rawPath) }

    private fun generateAntPath(rawPath: String): String {
        if (character === Character.DIGIT && numerus === Numerus.PLURAL) {
            return convertToAntPattern(rawPath, "{\\\\d+}")
        }
        if (character === Character.DIGIT && numerus === Numerus.SINGULAR) {
            return convertToAntPattern(rawPath, "{\\\\d}")
        }
        if (character === Character.LETTER && numerus === Numerus.PLURAL) {
            throw NotImplementedError()
        }
        if (character === Character.LETTER && numerus === Numerus.SINGULAR) {
            throw NotImplementedError()
        }
        return rawPath
    }

    private fun convertToAntPattern(rawPath: String, replacement: String): String {
        return if (rawPath.contains("{") && rawPath.contains("}")) {
            rawPath.replace("\\{.+}".toRegex(), replacement)
        } else rawPath
    }

}
