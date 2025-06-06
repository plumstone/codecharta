package de.maibornwolff.codecharta.analysers.parsers.gitlog.parser.git.helper

import de.maibornwolff.codecharta.analysers.parsers.gitlog.input.Modification
import de.maibornwolff.codecharta.util.Logger

class GitLogNumstatParsingHelper {
    companion object {
        private const val STANDARD_FILE_LINE_REGEX = "\\d+\\s+\\d+\\s+\\S+\\s*"
        private const val RENAME_FILE_LINE_REGEX = "\\d+\\s+\\d+\\s+\\S*\\S+ => \\S+\\S*\\s*"
        private const val RENAMING_SEPARATOR = "=>"
        private const val STANDARD_FILE_LINE_SPLITTER = "\\s+"
        private const val RENAME_FILE_LINE_SPLITTER = "[{}\\s+]"

        fun isFileLine(commitLine: String): Boolean {
            return commitLine.length >= 5 && (
                commitLine.matches(
                    STANDARD_FILE_LINE_REGEX.toRegex()
                ) || commitLine.matches(RENAME_FILE_LINE_REGEX.toRegex())
            )
        }

        fun parseModification(fileLine: String): Modification {
            if (fileLine.matches(STANDARD_FILE_LINE_REGEX.toRegex())) {
                return parseStandardModification(fileLine)
            } else if (fileLine.matches(RENAME_FILE_LINE_REGEX.toRegex())) {
                return parseRenameModification(fileLine)
            }

            return Modification.EMPTY
        }

        private fun String.removeDuplicateString(c: String): String {
            return this.replace(c + c, c)
        }

        private fun parseRenameModification(fileLine: String): Modification {
            val lineParts = fileLine.split(RENAME_FILE_LINE_SPLITTER.toRegex()).dropLastWhile({ it.isEmpty() })
            val additions = lineParts[0].toLong()
            val deletions = lineParts[1].toLong()
            var oldFileName: String
            var newFileName: String

            if (RENAMING_SEPARATOR == lineParts[4]) {
                oldFileName = lineParts[2] + lineParts[3] + if (lineParts.size > 6) lineParts[6] else ""
                if (lineParts[3].isEmpty()) {
                    oldFileName = oldFileName.removeDuplicateString("/")
                }

                newFileName = lineParts[2] + lineParts[5] + if (lineParts.size > 6) lineParts[6] else ""
                if (lineParts[5].isEmpty()) {
                    newFileName = newFileName.removeDuplicateString("/")
                }
            } else if (RENAMING_SEPARATOR == lineParts[3]) {
                oldFileName = lineParts[2]
                newFileName = lineParts[4]
            } else {
                Logger.warn { "Log line could not be parsed$fileLine" }
                return Modification.EMPTY
            }

            return Modification(newFileName, oldFileName, additions, deletions, Modification.Type.RENAME)
        }

        private fun parseStandardModification(fileLine: String): Modification {
            val lineParts = fileLine.split(STANDARD_FILE_LINE_SPLITTER.toRegex()).dropLastWhile({ it.isEmpty() })
            val additions = lineParts[0].toLong()
            val deletions = lineParts[1].toLong()
            val filename = lineParts[2]
            return Modification(filename.trim(), additions, deletions)
        }
    }
}
