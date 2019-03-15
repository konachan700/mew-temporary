#include "P256-cortex-ecdh.h"
#include "../system/system.h"

#define MEW_PUBKEY_SIZE 64

static uint8_t result_my_public_point[MEW_PUBKEY_SIZE];
static uint8_t private_key[32];

static void createRandomPKey(void) {
	uint32_t i;
	for (i=0; i<32; i++) {
		private_key[i] = (uint8_t) mew_random32();
	}
}

unsigned int mew_p256_ecdh_get_session_pubkey(char* key) {
	memcpy(key, result_my_public_point, MEW_PUBKEY_SIZE);
	return 64;
}

unsigned int mew_p256_ecdh_handler(void) {
	do {
		createRandomPKey();
	} while (!P256_ecdh_keygen(result_my_public_point, private_key));
	return 0;
}
