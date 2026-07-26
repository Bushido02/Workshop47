package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.player.PlayerClientEvents;
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

// АКТИВНЫЙ терминал (control_station.bbmodel, с анимацией idle). Раньше
// (первая версия 26.07.2026) это был один блок с полем active/inactive в
// BlockEntity - переделано по решению пользователя 26.07.2026 (позже, тот
// же день) на ДВА раздельных блока/BlockEntity/Item, каждый со своей
// зафиксированной моделью, без runtime-переключения. Если понадобится
// логика "разблокировать/заблокировать" - предполагается замена блока
// в мире (level.setBlock(pos, FORMIX_CONTROL_STATION_INACTIVE...)), а не
// смена состояния внутри одного блока.
//
// См. FormixControlStationInactiveBlock.java - та же логика клика/шейпа,
// продублирована сознательно (два маленьких класса проще поддерживать
// раздельно, чем одну ветвящуюся реализацию под два ещё не до конца
// определённых сценария использования).
public class FormixControlStationBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(-1.0, 0.0, -1.0, 2.0, 4.0, 2.0);

    public FormixControlStationBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FormixControlStationBlockEntity(blockPos, blockState);
    }

    @Override // render in FormixControlStationBlockRenderer (GeckoLib) -
    // ENTITYBLOCK_ANIMATED - официальная документация GeckoLib4 требует
    // именно это значение для блоков, рендерящихся через GeoBlockRenderer.
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
            // TODO (будущая сессия): когда появится экран "Познание/Мир/Я",
            // разделить relativeY на зоны точнее (сейчас только "низ = RTS
            // кнопка", всё остальное = PASS, экран не открывается).
            if (relativeY <= 0.85) {
                if (!PlayerClientEvents.isRTSPlayer()) {
                    return InteractionResult.PASS;
                }
                OrthoviewClientEvents.tryToToggleEnable();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }
}
