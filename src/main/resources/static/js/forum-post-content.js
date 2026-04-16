(function (window) {
    const IMAGE_MARKDOWN_REGEX = /!\[([^\]]*)\]\(((?:https?:\/\/|\/uploads\/)[^)\s"'<>]+)\)/g;
    const SAFE_IMAGE_URL_REGEX = /^(?:https?:\/\/|\/uploads\/)[^\s"'<>]+$/i;

    function escapeHtml(text) {
        return String(text || '').replace(/[&<>"']/g, function (char) {
            return {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;'
            }[char];
        });
    }

    function isSafeImageUrl(url) {
        return SAFE_IMAGE_URL_REGEX.test(String(url || '').trim());
    }

    function safeJsonParse(value) {
        try {
            return JSON.parse(value);
        } catch (error) {
            return [];
        }
    }

    function parseImages(imagesPayload) {
        if (!imagesPayload) {
            return [];
        }

        const source = Array.isArray(imagesPayload) ? imagesPayload : safeJsonParse(imagesPayload);
        if (!Array.isArray(source)) {
            return [];
        }

        return Array.from(new Set(
            source
                .map(item => String(item || '').trim())
                .filter(isSafeImageUrl)
        ));
    }

    function extractImageUrlsFromMarkdown(content) {
        const imageUrls = [];
        String(content || '').replace(IMAGE_MARKDOWN_REGEX, function (_, __, url) {
            if (isSafeImageUrl(url) && !imageUrls.includes(url)) {
                imageUrls.push(url);
            }
            return _;
        });
        return imageUrls;
    }

    function getImageUrls(post) {
        const payloadImages = parseImages(post && post.images);
        return payloadImages.length ? payloadImages : extractImageUrlsFromMarkdown(post && post.content);
    }

    function stripMarkdown(content) {
        return String(content || '')
            .replace(IMAGE_MARKDOWN_REGEX, function (_, alt) {
                return alt ? '[图片] ' + alt : '[图片]';
            })
            .replace(/`([^`]+)`/g, '$1')
            .replace(/\*\*([^*]+)\*\*/g, '$1')
            .replace(/\*([^*\n]+)\*/g, '$1')
            .replace(/\r\n/g, '\n')
            .replace(/\n+/g, ' ')
            .replace(/\s+/g, ' ')
            .trim();
    }

    function createExcerpt(content, maxLength) {
        const normalized = stripMarkdown(content);
        if (normalized.length <= maxLength) {
            return normalized;
        }
        return normalized.slice(0, maxLength).trimEnd() + '...';
    }

    function renderContent(content) {
        const images = [];
        let normalized = String(content || '').replace(/\r\n/g, '\n');

        normalized = normalized.replace(IMAGE_MARKDOWN_REGEX, function (_, alt, url) {
            const index = images.push({
                alt: alt || '帖子图片',
                url
            }) - 1;
            return '@@FORUM_IMAGE_' + index + '@@';
        });

        let html = escapeHtml(normalized)
            .replace(/`([^`]+)`/g, '<code>$1</code>')
            .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
            .replace(/\*([^*\n]+)\*/g, '<em>$1</em>')
            .replace(/\n/g, '<br>');

        images.forEach(function (image, index) {
            html = html.replace(
                '@@FORUM_IMAGE_' + index + '@@',
                '<img class="forum-rich-image" src="' + escapeHtml(image.url) + '" alt="' + escapeHtml(image.alt) + '" loading="lazy">'
            );
        });

        return html;
    }

    window.ForumPostContent = {
        createExcerpt,
        escapeHtml,
        extractImageUrlsFromMarkdown,
        getImageUrls,
        parseImages,
        renderContent
    };
})(window);
