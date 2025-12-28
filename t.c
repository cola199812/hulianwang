#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/wait.h>

#define SHM_SIZE 30

int main() {
    int shmid;
    char *shm_ptr;
    pid_t pid;
    
    // 1. 创建共享内存段（IPC_PRIVATE表示私有）
    printf("父进程: 创建30字节私有共享内存段...\n");
    shmid = shmget(IPC_PRIVATE, SHM_SIZE, IPC_CREAT | 0666);
    if (shmid == -1) {
        perror("shmget失败");
        exit(EXIT_FAILURE);
    }
    printf("父进程: 共享内存创建成功，标识符: %d\n", shmid);
    
    // 2. 父进程挂接共享内存
    shm_ptr = (char *)shmat(shmid, NULL, 0);
    if (shm_ptr == (char *)-1) {
        perror("父进程shmat失败");
        exit(EXIT_FAILURE);
    }
    printf("父进程: 已挂接共享内存，地址: %p\n", (void*)shm_ptr);
    
    // 3. 父进程写入大写字母
    char original_msg[] = "HELLO SHARED MEMORY 2024";
    strncpy(shm_ptr, original_msg, SHM_SIZE - 1);
    shm_ptr[SHM_SIZE - 1] = '\0'; // 确保字符串结束
    printf("父进程: 写入共享内存的内容: %s\n", shm_ptr);
    
    // 4. 创建子进程
    printf("父进程: 创建子进程...\n");
    pid = fork();
    
    if (pid < 0) {
        perror("fork失败");
        exit(EXIT_FAILURE);
    }
    
    if (pid == 0) { // 子进程代码
        printf("\n子进程: 开始执行 (PID=%d)\n", getpid());
        
        // 5. 子进程读取并显示共享内存内容
        printf("子进程: 读取共享内存内容: %s\n", shm_ptr);
        
        // 6. 子进程将大写字母转换为小写
        for (int i = 0; shm_ptr[i] != '\0' && i < SHM_SIZE; i++) {
            if (shm_ptr[i] >= 'A' && shm_ptr[i] <= 'Z') {
                shm_ptr[i] = shm_ptr[i] + ('a' - 'A');
            }
        }
        printf("子进程: 修改为小写后: %s\n", shm_ptr);
        
        // 7. 子进程脱接共享内存
        if (shmdt(shm_ptr) == -1) {
            perror("子进程shmdt失败");
            exit(EXIT_FAILURE);
        }
        printf("子进程: 已脱接共享内存\n");
        printf("子进程: 结束执行\n\n");
        
        exit(0); // 子进程退出
    } 
    else { // 父进程代码
        // 8. 父进程等待子进程完成
        wait(NULL);
        printf("父进程: 子进程已结束\n");
        
        // 9. 父进程睡眠5秒
        for (int i = 5; i > 0; i--) {
            sleep(1);
        }
        
        // 10. 父进程再次读取共享内存
        printf("父进程: 读取共享内存内容: %s\n", shm_ptr);
        
        // 11. 父进程脱接共享内存
        if (shmdt(shm_ptr) == -1) {
            perror("父进程shmdt失败");
            exit(EXIT_FAILURE);
        }
        
        // 12. 删除共享内存段
        if (shmctl(shmid, IPC_RMID, NULL) == -1) {
            perror("shmctl IPC_RMID失败");
            exit(EXIT_FAILURE);
        }
        
    }
    
    return 0;
}