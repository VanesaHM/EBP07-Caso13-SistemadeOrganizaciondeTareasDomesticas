ALTER TABLE app_users ADD COLUMN IF NOT EXISTS active boolean NOT NULL DEFAULT false;
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS email_confirmation_token varchar(255);
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS email_confirmation_expires_at timestamp;
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS phone varchar(255);
