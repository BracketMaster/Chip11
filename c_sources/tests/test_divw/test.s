
.section .text
.org 0
b _start
.org 0x10
_start:
generate_data:
lis 1, 0xdead,
ori 1, 1, 0xbeef
lis 2, 0x1234
ori 2, 2, 0x5678
li 3, 0x1000
li 4, -0x1000
li 5, -0x5a5a
li 6, 0x5a5a
li 7, -1
lis 8, 0x1234
ori 8, 8, 0x5678
sldi 8, 8, 32
oris 8, 8, 0x9abc
ori 8, 8, 0xdef0
mtcr 1

test_divw:
oris 17, 17, -874357142@h                         # 22
ori 17, 17, -874357142@l                          # 23
clrldi 18, 17, 0                                  # 24
divw 19, 8, 18                                    # 25
divwu 19, 8, 18                                   # 26
clrldi 18, 17, 1                                  # 27
divw 19, 3, 18                                    # 28
divwu 19, 3, 18                                   # 29
clrldi 18, 17, 2                                  # 30
divw 19, 4, 18                                    # 31
divwu 19, 4, 18                                   # 32
clrldi 18, 17, 3                                  # 33
divw 19, 6, 18                                    # 34
divwu 19, 6, 18                                   # 35
clrldi 18, 17, 4                                  # 36
divw 19, 8, 18                                    # 37
divwu 19, 8, 18                                   # 38
clrldi 18, 17, 5                                  # 39
divw 19, 4, 18                                    # 40
divwu 19, 4, 18                                   # 41
clrldi 18, 17, 6                                  # 42
divw 19, 6, 18                                    # 43
divwu 19, 6, 18                                   # 44
clrldi 18, 17, 7                                  # 45
divw 19, 3, 18                                    # 46
divwu 19, 3, 18                                   # 47
clrldi 18, 17, 8                                  # 48
divw 19, 5, 18                                    # 49
divwu 19, 5, 18                                   # 50
clrldi 18, 17, 9                                  # 51
divw 19, 1, 18                                    # 52
divwu 19, 1, 18                                   # 53
clrldi 18, 17, 10                                 # 54
divw 19, 3, 18                                    # 55
divwu 19, 3, 18                                   # 56
clrldi 18, 17, 11                                 # 57
divw 19, 3, 18                                    # 58
divwu 19, 3, 18                                   # 59
clrldi 18, 17, 12                                 # 60
divw 19, 2, 18                                    # 61
divwu 19, 2, 18                                   # 62
clrldi 18, 17, 13                                 # 63
divw 19, 3, 18                                    # 64
divwu 19, 3, 18                                   # 65
clrldi 18, 17, 14                                 # 66
divw 19, 2, 18                                    # 67
divwu 19, 2, 18                                   # 68
clrldi 18, 17, 15                                 # 69
divw 19, 5, 18                                    # 70
divwu 19, 5, 18                                   # 71
clrldi 18, 17, 16                                 # 72
divw 19, 6, 18                                    # 73
divwu 19, 6, 18                                   # 74
clrldi 18, 17, 17                                 # 75
divw 19, 7, 18                                    # 76
divwu 19, 7, 18                                   # 77
clrldi 18, 17, 18                                 # 78
divw 19, 1, 18                                    # 79
divwu 19, 1, 18                                   # 80
clrldi 18, 17, 19                                 # 81
divw 19, 7, 18                                    # 82
divwu 19, 7, 18                                   # 83
clrldi 18, 17, 20                                 # 84
divw 19, 8, 18                                    # 85
divwu 19, 8, 18                                   # 86
clrldi 18, 17, 21                                 # 87
divw 19, 4, 18                                    # 88
divwu 19, 4, 18                                   # 89
clrldi 18, 17, 22                                 # 90
divw 19, 1, 18                                    # 91
divwu 19, 1, 18                                   # 92
clrldi 18, 17, 23                                 # 93
divw 19, 2, 18                                    # 94
divwu 19, 2, 18                                   # 95
clrldi 18, 17, 24                                 # 96
divw 19, 7, 18                                    # 97
divwu 19, 7, 18                                   # 98
clrldi 18, 17, 25                                 # 99
divw 19, 4, 18                                    # 100
divwu 19, 4, 18                                   # 101
clrldi 18, 17, 26                                 # 102
divw 19, 8, 18                                    # 103
divwu 19, 8, 18                                   # 104
clrldi 18, 17, 27                                 # 105
divw 19, 7, 18                                    # 106
divwu 19, 7, 18                                   # 107
clrldi 18, 17, 28                                 # 108
divw 19, 2, 18                                    # 109
divwu 19, 2, 18                                   # 110
clrldi 18, 17, 29                                 # 111
divw 19, 4, 18                                    # 112
divwu 19, 4, 18                                   # 113
clrldi 18, 17, 30                                 # 114
divw 19, 6, 18                                    # 115
divwu 19, 6, 18                                   # 116
clrldi 18, 17, 31                                 # 117
divw 19, 6, 18                                    # 118
divwu 19, 6, 18                                   # 119
oris 17, 17, -1200067170@h                        # 120
ori 17, 17, -1200067170@l                         # 121
clrldi 18, 17, 0                                  # 122
divw 19, 4, 18                                    # 123
divwu 19, 4, 18                                   # 124
clrldi 18, 17, 1                                  # 125
divw 19, 3, 18                                    # 126
divwu 19, 3, 18                                   # 127
clrldi 18, 17, 2                                  # 128
divw 19, 8, 18                                    # 129
divwu 19, 8, 18                                   # 130
clrldi 18, 17, 3                                  # 131
divw 19, 5, 18                                    # 132
divwu 19, 5, 18                                   # 133
clrldi 18, 17, 4                                  # 134
divw 19, 4, 18                                    # 135
divwu 19, 4, 18                                   # 136
clrldi 18, 17, 5                                  # 137
divw 19, 3, 18                                    # 138
divwu 19, 3, 18                                   # 139
clrldi 18, 17, 6                                  # 140
divw 19, 2, 18                                    # 141
divwu 19, 2, 18                                   # 142
clrldi 18, 17, 7                                  # 143
divw 19, 4, 18                                    # 144
divwu 19, 4, 18                                   # 145
clrldi 18, 17, 8                                  # 146
divw 19, 4, 18                                    # 147
divwu 19, 4, 18                                   # 148
clrldi 18, 17, 9                                  # 149
divw 19, 5, 18                                    # 150
divwu 19, 5, 18                                   # 151
clrldi 18, 17, 10                                 # 152
divw 19, 6, 18                                    # 153
divwu 19, 6, 18                                   # 154
clrldi 18, 17, 11                                 # 155
divw 19, 8, 18                                    # 156
divwu 19, 8, 18                                   # 157
clrldi 18, 17, 12                                 # 158
divw 19, 7, 18                                    # 159
divwu 19, 7, 18                                   # 160
clrldi 18, 17, 13                                 # 161
divw 19, 6, 18                                    # 162
divwu 19, 6, 18                                   # 163
clrldi 18, 17, 14                                 # 164
divw 19, 5, 18                                    # 165
divwu 19, 5, 18                                   # 166
clrldi 18, 17, 15                                 # 167
divw 19, 7, 18                                    # 168
divwu 19, 7, 18                                   # 169
clrldi 18, 17, 16                                 # 170
divw 19, 2, 18                                    # 171
divwu 19, 2, 18                                   # 172
clrldi 18, 17, 17                                 # 173
divw 19, 8, 18                                    # 174
divwu 19, 8, 18                                   # 175
clrldi 18, 17, 18                                 # 176
divw 19, 4, 18                                    # 177
divwu 19, 4, 18                                   # 178
clrldi 18, 17, 19                                 # 179
divw 19, 2, 18                                    # 180
divwu 19, 2, 18                                   # 181
clrldi 18, 17, 20                                 # 182
divw 19, 6, 18                                    # 183
divwu 19, 6, 18                                   # 184
clrldi 18, 17, 21                                 # 185
divw 19, 5, 18                                    # 186
divwu 19, 5, 18                                   # 187
clrldi 18, 17, 22                                 # 188
divw 19, 6, 18                                    # 189
divwu 19, 6, 18                                   # 190
clrldi 18, 17, 23                                 # 191
divw 19, 5, 18                                    # 192
divwu 19, 5, 18                                   # 193
clrldi 18, 17, 24                                 # 194
divw 19, 2, 18                                    # 195
divwu 19, 2, 18                                   # 196
clrldi 18, 17, 25                                 # 197
divw 19, 4, 18                                    # 198
divwu 19, 4, 18                                   # 199
clrldi 18, 17, 26                                 # 200
divw 19, 5, 18                                    # 201
divwu 19, 5, 18                                   # 202
clrldi 18, 17, 27                                 # 203
divw 19, 1, 18                                    # 204
divwu 19, 1, 18                                   # 205
clrldi 18, 17, 28                                 # 206
divw 19, 4, 18                                    # 207
divwu 19, 4, 18                                   # 208
clrldi 18, 17, 29                                 # 209
divw 19, 3, 18                                    # 210
divwu 19, 3, 18                                   # 211
clrldi 18, 17, 30                                 # 212
divw 19, 4, 18                                    # 213
divwu 19, 4, 18                                   # 214
clrldi 18, 17, 31                                 # 215
divw 19, 1, 18                                    # 216
divwu 19, 1, 18                                   # 217
oris 17, 17, 679389078@h                          # 218
ori 17, 17, 679389078@l                           # 219
clrldi 18, 17, 0                                  # 220
divw 19, 6, 18                                    # 221
divwu 19, 6, 18                                   # 222
clrldi 18, 17, 1                                  # 223
divw 19, 2, 18                                    # 224
divwu 19, 2, 18                                   # 225
clrldi 18, 17, 2                                  # 226
divw 19, 6, 18                                    # 227
divwu 19, 6, 18                                   # 228
clrldi 18, 17, 3                                  # 229
divw 19, 6, 18                                    # 230
divwu 19, 6, 18                                   # 231
clrldi 18, 17, 4                                  # 232
divw 19, 8, 18                                    # 233
divwu 19, 8, 18                                   # 234
clrldi 18, 17, 5                                  # 235
divw 19, 5, 18                                    # 236
divwu 19, 5, 18                                   # 237
clrldi 18, 17, 6                                  # 238
divw 19, 4, 18                                    # 239
divwu 19, 4, 18                                   # 240
clrldi 18, 17, 7                                  # 241
divw 19, 8, 18                                    # 242
divwu 19, 8, 18                                   # 243
clrldi 18, 17, 8                                  # 244
divw 19, 6, 18                                    # 245
divwu 19, 6, 18                                   # 246
clrldi 18, 17, 9                                  # 247
divw 19, 5, 18                                    # 248
divwu 19, 5, 18                                   # 249
clrldi 18, 17, 10                                 # 250
divw 19, 4, 18                                    # 251
divwu 19, 4, 18                                   # 252
clrldi 18, 17, 11                                 # 253
divw 19, 2, 18                                    # 254
divwu 19, 2, 18                                   # 255
clrldi 18, 17, 12                                 # 256
divw 19, 1, 18                                    # 257
divwu 19, 1, 18                                   # 258
clrldi 18, 17, 13                                 # 259
divw 19, 7, 18                                    # 260
divwu 19, 7, 18                                   # 261
clrldi 18, 17, 14                                 # 262
divw 19, 5, 18                                    # 263
divwu 19, 5, 18                                   # 264
clrldi 18, 17, 15                                 # 265
divw 19, 7, 18                                    # 266
divwu 19, 7, 18                                   # 267
clrldi 18, 17, 16                                 # 268
divw 19, 4, 18                                    # 269
divwu 19, 4, 18                                   # 270
clrldi 18, 17, 17                                 # 271
divw 19, 7, 18                                    # 272
divwu 19, 7, 18                                   # 273
clrldi 18, 17, 18                                 # 274
divw 19, 4, 18                                    # 275
divwu 19, 4, 18                                   # 276
clrldi 18, 17, 19                                 # 277
divw 19, 1, 18                                    # 278
divwu 19, 1, 18                                   # 279
clrldi 18, 17, 20                                 # 280
divw 19, 7, 18                                    # 281
divwu 19, 7, 18                                   # 282
clrldi 18, 17, 21                                 # 283
divw 19, 3, 18                                    # 284
divwu 19, 3, 18                                   # 285
clrldi 18, 17, 22                                 # 286
divw 19, 8, 18                                    # 287
divwu 19, 8, 18                                   # 288
clrldi 18, 17, 23                                 # 289
divw 19, 7, 18                                    # 290
divwu 19, 7, 18                                   # 291
clrldi 18, 17, 24                                 # 292
divw 19, 5, 18                                    # 293
divwu 19, 5, 18                                   # 294
clrldi 18, 17, 25                                 # 295
divw 19, 6, 18                                    # 296
divwu 19, 6, 18                                   # 297
clrldi 18, 17, 26                                 # 298
divw 19, 4, 18                                    # 299
divwu 19, 4, 18                                   # 300
clrldi 18, 17, 27                                 # 301
divw 19, 4, 18                                    # 302
divwu 19, 4, 18                                   # 303
clrldi 18, 17, 28                                 # 304
divw 19, 4, 18                                    # 305
divwu 19, 4, 18                                   # 306
clrldi 18, 17, 29                                 # 307
divw 19, 3, 18                                    # 308
divwu 19, 3, 18                                   # 309
clrldi 18, 17, 30                                 # 310
divw 19, 3, 18                                    # 311
divwu 19, 3, 18                                   # 312
clrldi 18, 17, 31                                 # 313
divw 19, 5, 18                                    # 314
divwu 19, 5, 18                                   # 315
oris 17, 17, -1604114477@h                        # 316
ori 17, 17, -1604114477@l                         # 317
clrldi 18, 17, 0                                  # 318
divw 19, 6, 18                                    # 319
divwu 19, 6, 18                                   # 320
clrldi 18, 17, 1                                  # 321
divw 19, 7, 18                                    # 322
divwu 19, 7, 18                                   # 323
clrldi 18, 17, 2                                  # 324
divw 19, 2, 18                                    # 325
divwu 19, 2, 18                                   # 326
clrldi 18, 17, 3                                  # 327
divw 19, 2, 18                                    # 328
divwu 19, 2, 18                                   # 329
clrldi 18, 17, 4                                  # 330
divw 19, 5, 18                                    # 331
divwu 19, 5, 18                                   # 332
clrldi 18, 17, 5                                  # 333
divw 19, 4, 18                                    # 334
divwu 19, 4, 18                                   # 335
clrldi 18, 17, 6                                  # 336
divw 19, 7, 18                                    # 337
divwu 19, 7, 18                                   # 338
clrldi 18, 17, 7                                  # 339
divw 19, 5, 18                                    # 340
divwu 19, 5, 18                                   # 341
clrldi 18, 17, 8                                  # 342
divw 19, 2, 18                                    # 343
divwu 19, 2, 18                                   # 344
clrldi 18, 17, 9                                  # 345
divw 19, 8, 18                                    # 346
divwu 19, 8, 18                                   # 347
clrldi 18, 17, 10                                 # 348
divw 19, 3, 18                                    # 349
divwu 19, 3, 18                                   # 350
clrldi 18, 17, 11                                 # 351
divw 19, 5, 18                                    # 352
divwu 19, 5, 18                                   # 353
clrldi 18, 17, 12                                 # 354
divw 19, 1, 18                                    # 355
divwu 19, 1, 18                                   # 356
clrldi 18, 17, 13                                 # 357
divw 19, 2, 18                                    # 358
divwu 19, 2, 18                                   # 359
clrldi 18, 17, 14                                 # 360
divw 19, 6, 18                                    # 361
divwu 19, 6, 18                                   # 362
clrldi 18, 17, 15                                 # 363
divw 19, 4, 18                                    # 364
divwu 19, 4, 18                                   # 365
clrldi 18, 17, 16                                 # 366
divw 19, 7, 18                                    # 367
divwu 19, 7, 18                                   # 368
clrldi 18, 17, 17                                 # 369
divw 19, 1, 18                                    # 370
divwu 19, 1, 18                                   # 371
clrldi 18, 17, 18                                 # 372
divw 19, 2, 18                                    # 373
divwu 19, 2, 18                                   # 374
clrldi 18, 17, 19                                 # 375
divw 19, 8, 18                                    # 376
divwu 19, 8, 18                                   # 377
clrldi 18, 17, 20                                 # 378
divw 19, 4, 18                                    # 379
divwu 19, 4, 18                                   # 380
clrldi 18, 17, 21                                 # 381
divw 19, 7, 18                                    # 382
divwu 19, 7, 18                                   # 383
clrldi 18, 17, 22                                 # 384
divw 19, 3, 18                                    # 385
divwu 19, 3, 18                                   # 386
clrldi 18, 17, 23                                 # 387
divw 19, 5, 18                                    # 388
divwu 19, 5, 18                                   # 389
clrldi 18, 17, 24                                 # 390
divw 19, 2, 18                                    # 391
divwu 19, 2, 18                                   # 392
clrldi 18, 17, 25                                 # 393
divw 19, 4, 18                                    # 394
divwu 19, 4, 18                                   # 395
clrldi 18, 17, 26                                 # 396
divw 19, 8, 18                                    # 397
divwu 19, 8, 18                                   # 398
clrldi 18, 17, 27                                 # 399
divw 19, 7, 18                                    # 400
divwu 19, 7, 18                                   # 401
clrldi 18, 17, 28                                 # 402
divw 19, 7, 18                                    # 403
divwu 19, 7, 18                                   # 404
clrldi 18, 17, 29                                 # 405
divw 19, 4, 18                                    # 406
divwu 19, 4, 18                                   # 407
clrldi 18, 17, 30                                 # 408
divw 19, 7, 18                                    # 409
divwu 19, 7, 18                                   # 410
clrldi 18, 17, 31                                 # 411
divw 19, 8, 18                                    # 412
divwu 19, 8, 18                                   # 413
nop
li 31, 1
hang: b hang
.section .data
data:
