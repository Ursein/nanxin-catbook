/**
 * Mock 数据 — 没有后端时展示用的假数据
 * 所有猫咪均为虚构，仅供演示
 */

const mockCats = [
  {
    id: 1,
    name: '大橘',
    nickname: '橘座',
    status: 'ACTIVE',
    gender: 'MALE',
    sterilized: true,
    birthYear: '2020',
    weight: 6.5,
    locationArea: '东苑食堂',
    colourTags: '橘色',
    personalityTags: '贪吃;亲人;慵懒',
    personalityDesc: '大橘是东苑食堂的常驻猫，每天准时出现在食堂门口等待投喂。性格温顺，喜欢被人摸头，但不喜欢被抱。体型偏胖，正在减肥中。',
    coverPhotoUrl: '',
    likeCount: 128,
    followCount: 56,
    avgRating: '4.5',
    avgR1: '4.8',
    avgR2: '4.2',
    avgR3: '4.6',
    avgR4: '5.0',
    avgR5: '3.8',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 101, url: '', compressedUrl: '', description: '大橘在东苑食堂门口晒太阳', isLiked: false, likeCount: 42 },
      { id: 102, url: '', compressedUrl: '', description: '大橘正在享用午餐', isLiked: false, likeCount: 35 },
      { id: 103, url: '', compressedUrl: '', description: '大橘趴在草丛里打盹', isLiked: false, likeCount: 28 },
    ],
    comments: [
      { id: 1001, username: '猫友小明', content: '大橘真的超级可爱！每次路过东苑都要去看看它', createdAt: '2024-12-15 14:30' },
      { id: 1002, username: '爱猫人士', content: '今天给它带了猫条，吃得可开心了', createdAt: '2024-12-14 09:15' },
      { id: 1003, username: '路人甲', content: '这只猫好胖啊哈哈', createdAt: '2024-12-12 18:00' },
    ],
    recommendCats: [
      { catId: 2, name: '小花', nickname: '花姑娘', coverPhotoUrl: '' },
      { catId: 3, name: '芝麻', nickname: '', coverPhotoUrl: '' },
      { catId: 4, name: '小白', nickname: '白娘娘', coverPhotoUrl: '' },
    ],
  },
  {
    id: 2,
    name: '小花',
    nickname: '花姑娘',
    status: 'ACTIVE',
    gender: 'FEMALE',
    sterilized: true,
    birthYear: '2021',
    weight: 3.8,
    locationArea: '文德楼',
    colourTags: '三花',
    personalityTags: '优雅;独立;安静',
    personalityDesc: '小花是一只优雅的三花猫，常出没于文德楼附近。不太亲人但也不会躲人，喜欢安静地趴在花坛边看人来人往。',
    coverPhotoUrl: '',
    likeCount: 96,
    followCount: 42,
    avgRating: '4.2',
    avgR1: '4.0',
    avgR2: '4.8',
    avgR3: '3.5',
    avgR4: '3.8',
    avgR5: '4.0',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 201, url: '', compressedUrl: '', description: '小花在文德楼前花坛', isLiked: false, likeCount: 30 },
      { id: 202, url: '', compressedUrl: '', description: '小花优雅地坐着', isLiked: false, likeCount: 25 },
    ],
    comments: [
      { id: 2001, username: '文德楼常客', content: '每天上课都能看到小花，心情都变好了', createdAt: '2024-12-13 10:00' },
      { id: 2002, username: '猫奴一号', content: '三花真的太好看了！', createdAt: '2024-12-10 16:20' },
    ],
    recommendCats: [
      { catId: 1, name: '大橘', nickname: '橘座', coverPhotoUrl: '' },
      { catId: 5, name: '奶茶', nickname: '', coverPhotoUrl: '' },
      { catId: 6, name: '黑仔', nickname: '小黑', coverPhotoUrl: '' },
    ],
  },
  {
    id: 3,
    name: '芝麻',
    nickname: '',
    status: 'ACTIVE',
    gender: 'MALE',
    sterilized: false,
    birthYear: '2023',
    weight: 3.2,
    locationArea: '图书馆',
    colourTags: '黑白',
    personalityTags: '活泼;好动;好奇',
    personalityDesc: '芝麻是一只精力旺盛的奶牛猫，经常在图书馆附近追逐蝴蝶。好奇心极强，看到什么都想凑过去闻一闻。',
    coverPhotoUrl: '',
    likeCount: 67,
    followCount: 31,
    avgRating: '4.0',
    avgR1: '3.8',
    avgR2: '4.0',
    avgR3: '4.5',
    avgR4: '3.5',
    avgR5: '4.2',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 301, url: '', compressedUrl: '', description: '芝麻在图书馆台阶上', isLiked: false, likeCount: 20 },
      { id: 302, url: '', compressedUrl: '', description: '芝麻追逐蝴蝶', isLiked: false, likeCount: 18 },
    ],
    comments: [
      { id: 3001, username: '学霸猫', content: '在图书馆复习的时候经常看到芝麻，它比我还认真', createdAt: '2024-12-11 22:00' },
    ],
    recommendCats: [
      { catId: 1, name: '大橘', nickname: '橘座', coverPhotoUrl: '' },
      { catId: 7, name: '灰灰', nickname: '', coverPhotoUrl: '' },
      { catId: 8, name: '年糕', nickname: '', coverPhotoUrl: '' },
    ],
  },
  {
    id: 4,
    name: '小白',
    nickname: '白娘娘',
    status: 'ACTIVE',
    gender: 'FEMALE',
    sterilized: true,
    birthYear: '2019',
    weight: 4.0,
    locationArea: '宿舍区',
    colourTags: '白色',
    personalityTags: '温柔;黏人;爱干净',
    personalityDesc: '小白是宿舍区的明星猫，浑身雪白，非常爱干净。喜欢在宿舍楼下晒太阳，对经常投喂的同学特别亲近。',
    coverPhotoUrl: '',
    likeCount: 156,
    followCount: 78,
    avgRating: '4.7',
    avgR1: '4.9',
    avgR2: '4.8',
    avgR3: '4.7',
    avgR4: '4.0',
    avgR5: '4.5',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 401, url: '', compressedUrl: '', description: '小白在宿舍楼下晒太阳', isLiked: false, likeCount: 55 },
      { id: 402, url: '', compressedUrl: '', description: '小白优雅地舔毛', isLiked: false, likeCount: 40 },
      { id: 403, url: '', compressedUrl: '', description: '小白躺在草地上', isLiked: false, likeCount: 32 },
    ],
    comments: [
      { id: 4001, username: '宿舍楼长', content: '小白是我们宿舍的团宠！', createdAt: '2024-12-14 12:00' },
      { id: 4002, username: '猫粮大使', content: '每天都会来我们楼下，已经成了习惯', createdAt: '2024-12-13 08:30' },
      { id: 4003, username: '新同学', content: '第一次见到这么白的猫，太漂亮了', createdAt: '2024-12-10 19:00' },
    ],
    recommendCats: [
      { catId: 2, name: '小花', nickname: '花姑娘', coverPhotoUrl: '' },
      { catId: 5, name: '奶茶', nickname: '', coverPhotoUrl: '' },
      { catId: 1, name: '大橘', nickname: '橘座', coverPhotoUrl: '' },
    ],
  },
  {
    id: 5,
    name: '奶茶',
    nickname: '',
    status: 'SEEKING_ADOPT',
    gender: 'FEMALE',
    sterilized: true,
    birthYear: '2022',
    weight: 3.5,
    locationArea: '西苑',
    colourTags: '奶茶色',
    personalityTags: '亲人;温柔;胆小',
    personalityDesc: '奶茶是一只温柔的奶茶色猫咪，性格有些胆小但非常亲人。目前正在寻找领养家庭，希望能找到一个温暖的家。',
    coverPhotoUrl: '',
    likeCount: 89,
    followCount: 45,
    avgRating: '4.3',
    avgR1: '4.5',
    avgR2: '4.3',
    avgR3: '4.4',
    avgR4: '3.8',
    avgR5: '4.0',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 501, url: '', compressedUrl: '', description: '奶茶在西苑草坪上', isLiked: false, likeCount: 28 },
      { id: 502, url: '', compressedUrl: '', description: '奶茶怯生生地看着镜头', isLiked: false, likeCount: 22 },
    ],
    comments: [
      { id: 5001, username: '爱心人士', content: '好想领养奶茶！它看起来好温柔', createdAt: '2024-12-12 15:00' },
      { id: 5002, username: '西苑学姐', content: '奶茶性格真的很好，希望能找到好主人', createdAt: '2024-12-10 11:00' },
    ],
    recommendCats: [
      { catId: 2, name: '小花', nickname: '花姑娘', coverPhotoUrl: '' },
      { catId: 4, name: '小白', nickname: '白娘娘', coverPhotoUrl: '' },
      { catId: 6, name: '黑仔', nickname: '小黑', coverPhotoUrl: '' },
    ],
  },
  {
    id: 6,
    name: '黑仔',
    nickname: '小黑',
    status: 'ACTIVE',
    gender: 'MALE',
    sterilized: true,
    birthYear: '2021',
    weight: 5.0,
    locationArea: '体育馆',
    colourTags: '黑色',
    personalityTags: '神秘;独立;警觉',
    personalityDesc: '黑仔是一只纯黑色的猫，常出没于体育馆附近。行踪神秘，不太容易找到，但熟悉的人可以靠近。',
    coverPhotoUrl: '',
    likeCount: 45,
    followCount: 22,
    avgRating: '3.8',
    avgR1: '3.5',
    avgR2: '3.8',
    avgR3: '3.0',
    avgR4: '4.0',
    avgR5: '4.5',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 601, url: '', compressedUrl: '', description: '黑仔在体育馆阴影里', isLiked: false, likeCount: 15 },
      { id: 602, url: '', compressedUrl: '', description: '黑仔的绿色眼睛', isLiked: false, likeCount: 12 },
    ],
    comments: [
      { id: 6001, username: '体育生', content: '每次打完球都能看到黑仔在旁边', createdAt: '2024-12-09 17:00' },
    ],
    recommendCats: [
      { catId: 3, name: '芝麻', nickname: '', coverPhotoUrl: '' },
      { catId: 7, name: '灰灰', nickname: '', coverPhotoUrl: '' },
      { catId: 1, name: '大橘', nickname: '橘座', coverPhotoUrl: '' },
    ],
  },
  {
    id: 7,
    name: '灰灰',
    nickname: '',
    status: 'MISSING',
    gender: 'MALE',
    sterilized: false,
    birthYear: '2022',
    weight: 4.2,
    locationArea: '教学楼',
    colourTags: '灰白',
    personalityTags: '亲人;爱叫;贪玩',
    personalityDesc: '灰灰是一只灰白相间的猫咪，平时在教学楼附近活动。最近几天没有出现，如果有同学看到请及时联系我们。',
    coverPhotoUrl: '',
    likeCount: 112,
    followCount: 67,
    avgRating: '4.1',
    avgR1: '4.2',
    avgR2: '3.8',
    avgR3: '4.5',
    avgR4: '3.8',
    avgR5: '4.3',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 701, url: '', compressedUrl: '', description: '灰灰在教学楼前', isLiked: false, likeCount: 38 },
      { id: 702, url: '', compressedUrl: '', description: '灰灰对着镜头叫', isLiked: false, likeCount: 30 },
    ],
    comments: [
      { id: 7001, username: '担心的人', content: '灰灰去哪了？好几天没看到了', createdAt: '2024-12-13 20:00' },
      { id: 7002, username: '教学楼保安', content: '上周还在教学楼看到它，希望没事', createdAt: '2024-12-11 07:00' },
      { id: 7003, username: '爱猫协会', content: '我们已经在校内发布了寻猫启事', createdAt: '2024-12-10 14:00' },
    ],
    recommendCats: [
      { catId: 3, name: '芝麻', nickname: '', coverPhotoUrl: '' },
      { catId: 6, name: '黑仔', nickname: '小黑', coverPhotoUrl: '' },
      { catId: 8, name: '年糕', nickname: '', coverPhotoUrl: '' },
    ],
  },
  {
    id: 8,
    name: '年糕',
    nickname: '',
    status: 'DECEASED',
    gender: 'FEMALE',
    sterilized: true,
    birthYear: '2015',
    weight: 3.0,
    locationArea: '行政楼',
    colourTags: '白色',
    personalityTags: '安详;温柔;老猫',
    personalityDesc: '年糕是校园里最年长的猫咪之一，陪伴了无数届学生。2024年冬天安详离世，去了喵星。感谢它带给我们的所有温暖回忆。',
    coverPhotoUrl: '',
    likeCount: 234,
    followCount: 120,
    avgRating: '4.9',
    avgR1: '5.0',
    avgR2: '4.5',
    avgR3: '5.0',
    avgR4: '4.5',
    avgR5: '4.5',
    myR1: 0,
    myR2: 0,
    myR3: 0,
    myR4: 0,
    myR5: 0,
    isFollowed: false,
    photos: [
      { id: 801, url: '', compressedUrl: '', description: '年糕在行政楼前晒太阳', isLiked: false, likeCount: 88 },
      { id: 802, url: '', compressedUrl: '', description: '年糕安静地睡着', isLiked: false, likeCount: 72 },
      { id: 803, url: '', compressedUrl: '', description: '年糕和同学们在一起', isLiked: false, likeCount: 65 },
    ],
    comments: [
      { id: 8001, username: '毕业生', content: '年糕陪伴了我整个大学时光，永远怀念', createdAt: '2024-12-08 22:00' },
      { id: 8002, username: '老学长', content: '从大一到大四，年糕一直都在。RIP', createdAt: '2024-12-07 10:00' },
      { id: 8003, username: '猫协成员', content: '感谢年糕九年的陪伴，喵星没有病痛', createdAt: '2024-12-06 15:00' },
      { id: 8004, username: '匿名用户', content: '每次去行政楼办事都会看看年糕，以后再也看不到了', createdAt: '2024-12-05 09:00' },
    ],
    recommendCats: [
      { catId: 4, name: '小白', nickname: '白娘娘', coverPhotoUrl: '' },
      { catId: 1, name: '大橘', nickname: '橘座', coverPhotoUrl: '' },
      { catId: 2, name: '小花', nickname: '花姑娘', coverPhotoUrl: '' },
    ],
  },
]

// 按 status 筛选
function filterByStatus(cats, status) {
  if (!status) return cats
  return cats.filter((c) => c.status === status)
}

// 模拟分页
function paginate(arr, page, size) {
  const start = (page || 0) * (size || 20)
  return arr.slice(start, start + (size || 20))
}

/**
 * 模拟 catApi.list()
 * 返回格式与后端一致: { data: { content: [...], totalElements: N, ... } }
 */
export function getMockCatList(params = {}) {
  const { status, page = 0, size = 20 } = params
  let filtered = filterByStatus(mockCats, status)
  const content = paginate(filtered, page, size)
  return {
    data: {
      content,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      number: page,
      size,
    },
  }
}

/**
 * 模拟 catApi.detail()
 * 返回格式: { data: { ...cat } }
 */
export function getMockCatDetail(id) {
  const cat = mockCats.find((c) => c.id === Number(id))
  if (!cat) {
    throw new Error('Cat not found')
  }
  return { data: { ...cat } }
}

/**
 * 模拟 catApi.recommend()
 * 返回格式: { data: [...] }
 */
export function getMockRecommend(id) {
  const cat = mockCats.find((c) => c.id === Number(id))
  return { data: cat?.recommendCats || [] }
}

export { mockCats }

// ===== Mock 用户 & 状态 =====

const mockUser = {
  id: 1,
  username: 'admin',
  role: 'ADMIN',
  nickname: '管理员',
  email: 'admin@nuist.edu.cn',
}

// 客户端模拟状态（登录后可变）
const mockState = {
  followedCatIds: new Set([1, 2, 4]),
  ratings: {},
  nextCommentId: 9000,
  nextPhotoId: 900,
}

// ===== 认证相关 =====

export function getMockLogin(username, password) {
  // 任意用户名密码都能登录，始终返回管理员
  return {
    data: {
      token: 'mock-jwt-token-admin-' + Date.now(),
      user: { ...mockUser },
    },
  }
}

export function getMockRegister() {
  return { data: { message: '注册成功' } }
}

export function getMockMe() {
  return { data: { ...mockUser } }
}

export function getMockUpdateMe(data) {
  if (data.nickname) mockUser.nickname = data.nickname
  if (data.email) mockUser.email = data.email
  return { data: { ...mockUser } }
}

// ===== 关注相关 =====

export function getMockFollow(catId) {
  mockState.followedCatIds.add(Number(catId))
  return { data: true }
}

export function getMockUnfollow(catId) {
  mockState.followedCatIds.delete(Number(catId))
  return { data: true }
}

export function getMockFollowStatus(catId) {
  return { data: { followed: mockState.followedCatIds.has(Number(catId)) } }
}

export function getMockMyFollows() {
  return { data: [...mockState.followedCatIds] }
}

// ===== 评分相关 =====

export function getMockRatingStats(catId) {
  const cat = mockCats.find((c) => c.id === Number(catId))
  return {
    data: {
      avgR1: cat?.avgR1 || '0',
      avgR2: cat?.avgR2 || '0',
      avgR3: cat?.avgR3 || '0',
      avgR4: cat?.avgR4 || '0',
      avgR5: cat?.avgR5 || '0',
      totalCount: 5,
    },
  }
}

export function getMockRatingSubmit(catId, ratings) {
  mockState.ratings[Number(catId)] = ratings
  return { data: { message: '评分成功' } }
}

// ===== 评论相关 =====

export function getMockCommentCreate(catId, content) {
  const id = mockState.nextCommentId++
  const comment = {
    id,
    catId: Number(catId),
    username: mockUser.username,
    content,
    createdAt: new Date().toISOString().replace('T', ' ').substring(0, 16),
  }
  const cat = mockCats.find((c) => c.id === Number(catId))
  if (cat && cat.comments) {
    cat.comments.unshift(comment)
  }
  return { data: comment }
}

export function getMockCommentRemove(commentId) {
  for (const cat of mockCats) {
    if (cat.comments) {
      const idx = cat.comments.findIndex((c) => c.id === Number(commentId))
      if (idx >= 0) {
        cat.comments.splice(idx, 1)
        break
      }
    }
  }
  return { data: true }
}

// ===== 照片相关 =====

export function getMockPhotoList(catId) {
  const cat = mockCats.find((c) => c.id === Number(catId))
  return { data: cat?.photos || [] }
}

export function getMockPhotoUpload(catId) {
  const id = mockState.nextPhotoId++
  const photo = {
    id,
    url: '',
    compressedUrl: '',
    description: '新上传的照片',
    isLiked: false,
    likeCount: 0,
  }
  const cat = mockCats.find((c) => c.id === Number(catId))
  if (cat && cat.photos) {
    cat.photos.push(photo)
  }
  return { data: photo }
}

export function getMockPhotoLike() {
  return { data: true }
}

export function getMockPhotoUnlike() {
  return { data: true }
}

export function getMockPhotoApprove() {
  return { data: true }
}

export function getMockPhotoReject() {
  return { data: true }
}

export function getMockPhotoRemove() {
  return { data: true }
}

// ===== 用户相关 =====

export function getMockMyCats() {
  // 管理员添加了 id 为 1, 2, 4 的猫咪
  const myCatIds = [1, 2, 4]
  return { data: mockCats.filter((c) => myCatIds.includes(c.id)) }
}

export function getMockMyRatings() {
  const result = []
  for (const [catId, ratings] of Object.entries(mockState.ratings)) {
    const cat = mockCats.find((c) => c.id === Number(catId))
    if (cat) {
      result.push({ catId: Number(catId), catName: cat.name, ...ratings })
    }
  }
  return { data: result }
}