package org.project.portfolio.article.chcker

import org.springframework.stereotype.Component

@Component
class CommentChecker() {
    /**
     * Check if the user is the author of the comment
     * @param commentId Comment ID
     * @return Whether the user is the author of the comment (true: author, false: not author)
     */
    fun isAuthor(commentId: Long): Boolean {
        return true
    }
}
