package com.gamestore.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LegacySchemaRepairRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public LegacySchemaRepairRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        repairCartItems();
        repairOrders();
        repairOrderItems();
        repairUserGames();
        repairPointTransactions();
        repairUserDiscountCards();
    }

    private void repairCartItems() {
        if (!tableExists("cart_items")) {
            return;
        }

        jdbcTemplate.execute("""
            DELETE ci
            FROM cart_items ci
            LEFT JOIN games g ON ci.game_id = g.id
            WHERE ci.game_id IS NULL OR ci.game_id = 0 OR g.id IS NULL
            """);

        dropForeignKeys("cart_items", "product_id");
        dropIndexIfExists("cart_items", "uk_user_product");
        dropIndexIfExists("cart_items", "product_id");

        if (columnExists("cart_items", "product_id")) {
            jdbcTemplate.execute("ALTER TABLE cart_items DROP COLUMN product_id");
        }

        if (columnExists("cart_items", "selected")) {
            jdbcTemplate.execute("ALTER TABLE cart_items MODIFY COLUMN selected BIT(1) NOT NULL DEFAULT b'1'");
        }

        jdbcTemplate.execute("""
            DELETE c1
            FROM cart_items c1
            INNER JOIN cart_items c2
                ON c1.user_id = c2.user_id
               AND c1.game_id = c2.game_id
               AND c1.id > c2.id
            """);

        if (!indexExists("cart_items", "uk_cart_items_user_game")) {
            jdbcTemplate.execute("ALTER TABLE cart_items ADD CONSTRAINT uk_cart_items_user_game UNIQUE (user_id, game_id)");
        }

        if (!foreignKeyExists("cart_items", "fk_cart_items_game")) {
            jdbcTemplate.execute("ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE");
        }
    }

    private void repairOrders() {
        if (!tableExists("orders")) {
            return;
        }

        if (columnExists("orders", "order_number")) {
            jdbcTemplate.execute("""
                UPDATE orders
                SET order_number = COALESCE(NULLIF(order_no, ''), CONCAT('LEGACY-', id))
                WHERE order_number IS NULL OR order_number = ''
                """);
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN order_number VARCHAR(50) NULL");
        }

        if (columnExists("orders", "final_amount")) {
            jdbcTemplate.execute("""
                UPDATE orders
                SET final_amount = COALESCE(final_amount, payable_amount, total_amount, 0)
                WHERE final_amount IS NULL
                """);
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN final_amount DECIMAL(10,2) NULL DEFAULT 0.00");
        }

        if (columnExists("orders", "payment_method")) {
            jdbcTemplate.execute("""
                ALTER TABLE orders
                MODIFY COLUMN payment_method ENUM('MOCK','FREE','CREDIT_CARD','PAYPAL','ALIPAY','WECHAT') NULL DEFAULT 'MOCK'
                """);
        }

        if (columnExists("orders", "coupon_discount_amount")) {
            jdbcTemplate.execute("""
                UPDATE orders
                SET coupon_discount_amount = 0.00
                WHERE coupon_discount_amount IS NULL
                """);
        }

        if (columnExists("orders", "points_used")) {
            jdbcTemplate.execute("""
                UPDATE orders
                SET points_used = 0
                WHERE points_used IS NULL
                """);
        }

        if (columnExists("orders", "points_discount_amount")) {
            jdbcTemplate.execute("""
                UPDATE orders
                SET points_discount_amount = 0.00
                WHERE points_discount_amount IS NULL
                """);
        }
    }

    private void repairOrderItems() {
        if (!tableExists("order_items")) {
            return;
        }

        dropForeignKeys("order_items", "product_id");

        if (columnExists("order_items", "product_id")) {
            jdbcTemplate.execute("ALTER TABLE order_items MODIFY COLUMN product_id BIGINT NULL");
        }

        if (columnExists("order_items", "product_name")) {
            jdbcTemplate.execute("""
                UPDATE order_items
                SET product_name = COALESCE(NULLIF(game_name, ''), product_name)
                WHERE product_name IS NULL OR product_name = ''
                """);
            jdbcTemplate.execute("ALTER TABLE order_items MODIFY COLUMN product_name VARCHAR(200) NULL");
        }

        if (columnExists("order_items", "total_price")) {
            jdbcTemplate.execute("""
                UPDATE order_items
                SET total_price = COALESCE(total_price, subtotal, 0)
                WHERE total_price IS NULL
                """);
            jdbcTemplate.execute("ALTER TABLE order_items MODIFY COLUMN total_price DECIMAL(10,2) NULL DEFAULT 0.00");
        }
    }

    private void repairUserGames() {
        if (!tableExists("user_games")) {
            return;
        }

        if (columnExists("user_games", "acquired_price")) {
            jdbcTemplate.execute("UPDATE user_games SET acquired_price = 0.00 WHERE acquired_price IS NULL");
        }

        if (columnExists("user_games", "acquired_at") && columnExists("user_games", "purchase_date")) {
            jdbcTemplate.execute("""
                UPDATE user_games
                SET acquired_at = COALESCE(acquired_at, purchase_date, NOW())
                WHERE acquired_at IS NULL
                """);
        }
    }

    private void repairPointTransactions() {
        if (!tableExists("point_transactions")) {
            return;
        }

        if (!foreignKeyExists("point_transactions", "fk_point_transactions_user")) {
            jdbcTemplate.execute("""
                ALTER TABLE point_transactions
                ADD CONSTRAINT fk_point_transactions_user
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                """);
        }
    }

    private void repairUserDiscountCards() {
        if (!tableExists("user_discount_cards")) {
            return;
        }

        if (!foreignKeyExists("user_discount_cards", "fk_user_discount_cards_user")) {
            jdbcTemplate.execute("""
                ALTER TABLE user_discount_cards
                ADD CONSTRAINT fk_user_discount_cards_user
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                """);
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?
            """, Integer.class, tableName);
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
            """, Integer.class, tableName, columnName);
        return count != null && count > 0;
    }

    private boolean indexExists(String tableName, String indexName) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?
            """, Integer.class, tableName, indexName);
        return count != null && count > 0;
    }

    private boolean foreignKeyExists(String tableName, String constraintName) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND CONSTRAINT_NAME = ? AND CONSTRAINT_TYPE = 'FOREIGN KEY'
            """, Integer.class, tableName, constraintName);
        return count != null && count > 0;
    }

    private void dropForeignKeys(String tableName, String columnName) {
        List<String> foreignKeys = jdbcTemplate.queryForList("""
            SELECT CONSTRAINT_NAME
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = ?
              AND COLUMN_NAME = ?
              AND REFERENCED_TABLE_NAME IS NOT NULL
            """, String.class, tableName, columnName);

        for (String foreignKey : foreignKeys) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP FOREIGN KEY " + foreignKey);
        }
    }

    private void dropIndexIfExists(String tableName, String indexName) {
        if (indexExists(tableName, indexName)) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP INDEX " + indexName);
        }
    }
}
