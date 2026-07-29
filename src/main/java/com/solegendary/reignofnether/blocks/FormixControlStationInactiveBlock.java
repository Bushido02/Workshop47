package com.solegendary.reignofnether.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// НЕАКТИВНЫЙ терминал (control_station_diactive.bbmodel, статичная модель,
// без анимации). ЗАБЛОКИРОВАН до разблокировки (29.07.2026, по решению
// пользователя): клик по нижней панели больше НЕ переключает RTS-режим
// напрямую - вместо этого открывает FormixControlStationUnlockScreen с
// кнопкой "Разблокировать (10 Эйдоса)". При подтверждении клиент шлёт
// FormixControlStationServerboundPacket, сервер списывает Эйдос (реальный
// player.totalExperience) и заменяет блок в мире на FormixControlStationBlock
// (активный) - см. эти два файла для деталей. После разблокировки
// дальнейшие клики по терминалу идут уже через активный блок
// (FormixControlStationBlock.use()), который по-прежнему переключает RTS
// напрямую без экрана.
public class FormixControlStationInactiveBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(-1.0, 0.0, -1.0, 2.0, 4.0, 2.0);

    public FormixControlStationInactiveBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FormixControlStationInactiveBlockEntity(blockPos, blockState);
    }

    @Override // render in FormixControlStationInactiveBlockRenderer (GeckoLib)
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        double relativeY = hit.getLocation().y - pos.getY();

        if (level.isClientSide) {
            // Только нижняя зона реагирует - симметрично активному терминалу и
            // экрану "Познание/Мир/Я" (ещё не реализован), который со временем
            // должен занять оставшуюся площадь клика.
            if (relativeY <= 0.85) {
                // Разблокировка не требует, чтобы игрок уже был в RTS-матче
                // (в отличие от активного терминала) - это точка ВХОДА в RTS
                // для игрока, который прогрессирует вне матча.
                Minecraft.getInstance().setScreen(
                        new FormixControlStationUnlockScreen(pos, FormixControlStationServerboundPacket.EIDOS_UNLOCK_COST));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }
}
